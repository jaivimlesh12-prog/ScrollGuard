package com.scrollguard.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service providing a draggable floating counter bubble
 * displayed over Instagram / YouTube showing real-time reel counts and limits.
 */
@AndroidEntryPoint
class FloatingCounterService : Service() {

    @Inject
    lateinit var reelRepository: ReelRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var counterTextView: TextView? = null

    private var currentCount = 0
    private var dailyLimit = 50

    companion object {
        const val ACTION_SHOW = "com.scrollguard.ACTION_SHOW_OVERLAY"
        const val ACTION_HIDE = "com.scrollguard.ACTION_HIDE_OVERLAY"
        const val ACTION_INCREMENT = "com.scrollguard.ACTION_INCREMENT_OVERLAY"
        private const val NOTIFICATION_ID = 2001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForeground(NOTIFICATION_ID, buildNotification())
        setupFloatingBubble()
        observeStats()
    }

    private fun observeStats() {
        serviceScope.launch {
            combine(
                reelRepository.getTotalReelsToday(),
                settingsRepository.dailyReelLimit
            ) { count, limit ->
                Pair(count ?: 0, limit)
            }.collect { (count, limit) ->
                currentCount = count
                dailyLimit = limit
                updateBubbleUi()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingBubble() {
        if (floatingView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 200
        }

        val bubble = TextView(this).apply {
            setPadding(32, 16, 32, 16)
            setTextColor(Color.WHITE)
            textSize = 14f
            text = "🎬 $currentCount / $dailyLimit"
            elevation = 16f
            background = createBubbleBackground(Color.parseColor("#4CAF50"))
        }
        counterTextView = bubble
        floatingView = bubble

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        bubble.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(floatingView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val dx = Math.abs(event.rawX - initialTouchX)
                    val dy = Math.abs(event.rawY - initialTouchY)
                    if (dx < 10 && dy < 10) {
                        // Click event: open app
                        val launchIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(launchIntent)
                    }
                    true
                }
                else -> false
            }
        }

        try {
            windowManager?.addView(floatingView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateBubbleUi() {
        val tv = counterTextView ?: return
        tv.text = "🎬 $currentCount / $dailyLimit"

        val ratio = if (dailyLimit > 0) currentCount.toFloat() / dailyLimit else 0f
        val bgColor = when {
            ratio < 0.5f -> Color.parseColor("#4CAF50") // Green
            ratio < 0.8f -> Color.parseColor("#FF9800") // Yellow/Orange
            else -> Color.parseColor("#E53935") // Red
        }
        tv.background = createBubbleBackground(bgColor)
    }

    private fun createBubbleBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f
            setColor(color)
            setStroke(2, Color.WHITE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_HIDE -> stopSelf()
            ACTION_INCREMENT -> {
                // Instantly update UI on scroll signal
                currentCount++
                updateBubbleUi()
            }
        }
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_MONITORING)
            .setContentTitle("ScrollGuard Overlay Active")
            .setContentText("Tracking reels and shorts count")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        if (floatingView != null) {
            try {
                windowManager?.removeView(floatingView)
            } catch (e: Exception) {
                // View already detached
            }
            floatingView = null
        }
        super.onDestroy()
    }
}
