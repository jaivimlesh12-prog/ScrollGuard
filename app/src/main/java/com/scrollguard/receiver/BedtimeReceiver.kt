package com.scrollguard.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver handling scheduled bedtime alarms to notify the user to sleep
 * and optionally activate bedtime app restrictions.
 */
class BedtimeReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BedtimeEntryPoint {
        fun settingsRepository(): SettingsRepository
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            BedtimeEntryPoint::class.java
        )
        val settingsRepository = hiltEntryPoint.settingsRepository()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                settingsRepository.setFocusModeActive(true)

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ALERTS)
                    .setContentTitle("🌙 Bedtime Wind Down")
                    .setContentText("It's time to put your phone away and get some rest!")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.notify(5001, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
