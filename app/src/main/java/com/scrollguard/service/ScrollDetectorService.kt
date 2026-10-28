package com.scrollguard.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.scrollguard.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Accessibility Service that monitors scroll events in Instagram and YouTube
 * to count reels/shorts, and also monitors browser URLs for web-based reel viewing.
 */
class ScrollDetectorService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastScrollTime = 0L
    private var lastPackageName = ""
    private var currentPackageName = ""
    private var isInReelsSection = false
    private var isInShortsSection = false
    private val scrollDebounceMs = 800L

    private var currentBrowserUrl = ""

    companion object {
        const val ACTION_REEL_SCROLLED = "com.scrollguard.REEL_SCROLLED"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_SOURCE = "source"
        const val EXTRA_BROWSER_PACKAGE = "browser_package"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return

        when {
            packageName == Constants.INSTAGRAM_PACKAGE -> handleInstagramEvent(event)
            packageName == Constants.YOUTUBE_PACKAGE -> handleYouTubeEvent(event)
            packageName in Constants.COMMON_BROWSERS -> handleBrowserEvent(event, packageName)
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            currentPackageName = packageName
        }
    }

    private fun handleInstagramEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                isInReelsSection = detectInstagramReelsSection(event)
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                if (isInReelsSection) {
                    onReelScrollDetected(Constants.INSTAGRAM_PACKAGE, "app", null)
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (isInReelsSection && isVerticalSwipe(event)) {
                    onReelScrollDetected(Constants.INSTAGRAM_PACKAGE, "app", null)
                }
            }
        }
    }

    private fun handleYouTubeEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                isInShortsSection = detectYoutubeShortsSection(event)
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                if (isInShortsSection) {
                    onReelScrollDetected(Constants.YOUTUBE_PACKAGE, "app", null)
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (isInShortsSection && isVerticalSwipe(event)) {
                    onReelScrollDetected(Constants.YOUTUBE_PACKAGE, "app", null)
                }
            }
        }
    }

    private fun handleBrowserEvent(event: AccessibilityEvent, browserPackage: String) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED
        ) {
            val url = extractBrowserUrl(event)
            if (url != null) {
                currentBrowserUrl = url
            }

            val targetPackage = when {
                currentBrowserUrl.contains(Constants.INSTAGRAM_REELS_URL, ignoreCase = true) -> Constants.INSTAGRAM_PACKAGE
                currentBrowserUrl.contains(Constants.YOUTUBE_SHORTS_URL, ignoreCase = true) -> Constants.YOUTUBE_PACKAGE
                else -> null
            }

            if (targetPackage != null && event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                onReelScrollDetected(targetPackage, "browser", browserPackage)
            }
        }
    }

    private fun onReelScrollDetected(packageName: String, source: String, browserPackage: String?) {
        val now = System.currentTimeMillis()
        if (now - lastScrollTime < scrollDebounceMs) return
        lastScrollTime = now

        val intent = Intent(ACTION_REEL_SCROLLED).apply {
            putExtra(EXTRA_PACKAGE_NAME, packageName)
            putExtra(EXTRA_SOURCE, source)
            browserPackage?.let { putExtra(EXTRA_BROWSER_PACKAGE, it) }
            setPackage(this@ScrollDetectorService.packageName)
        }
        sendBroadcast(intent)

        // Broadcast to floating overlay to update counter live
        val overlayIntent = Intent(this, FloatingCounterService::class.java).apply {
            action = FloatingCounterService.ACTION_INCREMENT
            putExtra(EXTRA_PACKAGE_NAME, packageName)
        }
        try {
            startService(overlayIntent)
        } catch (e: Exception) {
            // Android O background execution limitation fallback
        }
    }

    private fun detectInstagramReelsSection(event: AccessibilityEvent): Boolean {
        val root = rootInActiveWindow ?: return isInReelsSection
        return try {
            val reelsTab = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_tab")
            val reelsViewer = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_view_pager")
            val className = event.className?.toString() ?: ""
            val isReelsActivity = className.contains("clips", ignoreCase = true) ||
                    className.contains("reel", ignoreCase = true)

            reelsTab.isNotEmpty() || reelsViewer.isNotEmpty() || isReelsActivity
        } catch (e: Exception) {
            isInReelsSection
        }
    }

    private fun detectYoutubeShortsSection(event: AccessibilityEvent): Boolean {
        val root = rootInActiveWindow ?: return isInShortsSection
        return try {
            val shortsContainer = root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_recycler")
            val className = event.className?.toString() ?: ""
            val isShortsActivity = className.contains("shorts", ignoreCase = true) ||
                    className.contains("reel", ignoreCase = true)

            shortsContainer.isNotEmpty() || isShortsActivity
        } catch (e: Exception) {
            isInShortsSection
        }
    }

    private fun extractBrowserUrl(event: AccessibilityEvent): String? {
        val root = rootInActiveWindow ?: return null
        return try {
            val urlBarIds = listOf(
                "com.android.chrome:id/url_bar",
                "com.android.chrome:id/search_box_text",
                "org.mozilla.firefox:id/url_bar_title",
                "org.mozilla.firefox:id/mozac_browser_toolbar_url_view",
                "com.brave.browser:id/url_bar",
                "com.opera.browser:id/url_field",
                "com.microsoft.emmx:id/url_bar"
            )

            for (viewId in urlBarIds) {
                val nodes = root.findAccessibilityNodeInfosByViewId(viewId)
                if (nodes.isNotEmpty()) {
                    val text = nodes[0].text?.toString()
                    if (!text.isNullOrBlank()) return text
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun isVerticalSwipe(event: AccessibilityEvent): Boolean {
        return event.scrollY > 0 || event.scrollDeltaY != 0
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
