package com.scrollguard.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.scrollguard.data.repository.AppLimitRepository
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.ScreenTimeRepository
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants
import com.scrollguard.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Core foreground service that monitors screen time and records reel scroll events.
 * It also checks app usage limits and triggers blocking when limits are reached.
 */
@AndroidEntryPoint
class ScreenTimeService : Service() {

    @Inject
    lateinit var screenTimeRepository: ScreenTimeRepository

    @Inject
    lateinit var reelRepository: ReelRepository

    @Inject
    lateinit var appLimitRepository: AppLimitRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var totalScreenTimeMs: Long = 0
    private var totalReels: Int = 0

    companion object {
        private const val NOTIFICATION_ID = 1001
    }

    private val reelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ScrollDetectorService.ACTION_REEL_SCROLLED) {
                val pkg = intent.getStringExtra(ScrollDetectorService.EXTRA_PACKAGE_NAME) ?: return
                val source = intent.getStringExtra(ScrollDetectorService.EXTRA_SOURCE) ?: "app"
                val browserPkg = intent.getStringExtra(ScrollDetectorService.EXTRA_BROWSER_PACKAGE)

                serviceScope.launch {
                    reelRepository.recordReelScroll(pkg, source, browserPkg)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification("Monitoring active..."))

        val filter = IntentFilter(ScrollDetectorService.ACTION_REEL_SCROLLED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(reelReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(reelReceiver, filter)
        }

        startMonitoringLoop()
        observeStatsForNotification()
    }

    private fun startMonitoringLoop() {
        serviceScope.launch {
            while (isActive) {
                try {
                    screenTimeRepository.syncUsageStats()
                    checkLimits()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(30_000L) // Poll every 30 seconds
            }
        }
    }

    private suspend fun checkLimits() {
        val limits = appLimitRepository.getEnabledLimits().first()
        if (limits.isEmpty()) return

        val todayUsage = screenTimeRepository.getScreenTimeForToday().first()
        val usageMap = todayUsage.associateBy({ it.packageName }, { it.usageMs })

        for (limit in limits) {
            val usage = usageMap[limit.packageName] ?: 0L
            if (limit.dailyLimitMs in 1..usage) {
                // Limit exceeded -> trigger app blocker
                val blockIntent = Intent(this@ScreenTimeService, AppBlockerService::class.java).apply {
                    putExtra(AppBlockerService.EXTRA_PACKAGE_NAME, limit.packageName)
                    putExtra(AppBlockerService.EXTRA_APP_NAME, limit.appName)
                    putExtra(AppBlockerService.EXTRA_IS_STRICT, limit.isStrict)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(blockIntent)
                } else {
                    startService(blockIntent)
                }
            }
        }
    }

    private fun observeStatsForNotification() {
        serviceScope.launch {
            screenTimeRepository.getTotalScreenTimeToday().collect { timeMs ->
                totalScreenTimeMs = timeMs ?: 0L
                updateNotification()
            }
        }
        serviceScope.launch {
            reelRepository.getTotalReelsToday().collect { count ->
                totalReels = count ?: 0
                updateNotification()
            }
        }
    }

    private fun updateNotification() {
        val formattedTime = DateUtils.formatDuration(totalScreenTimeMs)
        val text = "Screen Time: $formattedTime • Reels: $totalReels"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_MONITORING)
            .setContentTitle("ScrollGuard")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        try {
            unregisterReceiver(reelReceiver)
        } catch (e: Exception) {
            // Already unregistered
        }
        super.onDestroy()
    }
}
