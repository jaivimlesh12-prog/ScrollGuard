package com.scrollguard.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.scrollguard.util.Constants
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for ScrollGuard.
 *
 * Initializes Hilt dependency injection and creates notification channels
 * used throughout the app for monitoring, alerts, reports, blocking, and
 * eye-strain reminders.
 */
@HiltAndroidApp
class ScrollGuardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    /**
     * Creates all notification channels required by ScrollGuard.
     *
     * Channels are only created on Android O (API 26) and above.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val channels = listOf(
                NotificationChannel(
                    Constants.CHANNEL_MONITORING,
                    "Monitoring Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Persistent notification for the usage monitoring service"
                },
                NotificationChannel(
                    Constants.CHANNEL_ALERTS,
                    "Usage Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when usage limits are approaching or exceeded"
                },
                NotificationChannel(
                    Constants.CHANNEL_DAILY,
                    "Daily Reports",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily usage summary reports"
                },
                NotificationChannel(
                    Constants.CHANNEL_BLOCKER,
                    "App Blocker",
                    NotificationManager.IMPORTANCE_MAX
                ).apply {
                    description = "Overlay notifications when a blocked app is opened"
                },
                NotificationChannel(
                    Constants.CHANNEL_EYESTRAIN,
                    "Eye Strain Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Periodic reminders to take a break and rest your eyes"
                }
            )

            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }
}
