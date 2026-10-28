package com.scrollguard.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants
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
 * Service that runs in the background to provide 20-20-20 eye strain reminders.
 * Every 20 minutes of continuous screen time, it alerts the user to take a 20-second break.
 */
@AndroidEntryPoint
class EyeStrainReminderService : Service() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    companion object {
        private const val NOTIFICATION_ID = 4001
        private const val ALERT_NOTIFICATION_ID = 4002
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildOngoingNotification())
        startReminderLoop()
    }

    private fun startReminderLoop() {
        serviceScope.launch {
            while (isActive) {
                val intervalMin = settingsRepository.eyeStrainIntervalMin.first()
                delay(intervalMin * 60 * 1000L)

                val isEnabled = settingsRepository.isEyeStrainEnabled.first()
                if (isEnabled) {
                    showEyeBreakAlert()
                }
            }
        }
    }

    private fun showEyeBreakAlert() {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val alert = NotificationCompat.Builder(this, Constants.CHANNEL_EYESTRAIN)
            .setContentTitle("👁️ Eye Break Reminder (20-20-20 Rule)")
            .setContentText("Look at something 20 feet away for 20 seconds to rest your eyes!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(ALERT_NOTIFICATION_ID, alert)
    }

    private fun buildOngoingNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_MONITORING)
            .setContentTitle("Eye Strain Protection Active")
            .setContentText("ScrollGuard is timing your healthy screen breaks")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
