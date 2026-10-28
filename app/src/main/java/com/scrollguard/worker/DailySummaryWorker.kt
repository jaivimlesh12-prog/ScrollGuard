package com.scrollguard.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.ScreenTimeRepository
import com.scrollguard.ui.MainActivity
import com.scrollguard.util.Constants
import com.scrollguard.util.DateUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker that generates and displays a daily evening usage summary notification.
 */
class DailySummaryWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DailySummaryEntryPoint {
        fun screenTimeRepository(): ScreenTimeRepository
        fun reelRepository(): ReelRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                DailySummaryEntryPoint::class.java
            )

            val screenTimeRepo = entryPoint.screenTimeRepository()
            val reelRepo = entryPoint.reelRepository()

            val totalScreenTimeMs = screenTimeRepo.getTotalScreenTimeToday().first() ?: 0L
            val totalReels = reelRepo.getTotalReelsToday().first() ?: 0
            val totalPickups = screenTimeRepo.getPickupCountToday().first() ?: 0

            val formattedTime = DateUtils.formatDuration(totalScreenTimeMs)

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, Constants.CHANNEL_DAILY)
                .setContentTitle("📊 Today's Digital Wrap-Up")
                .setContentText("Screen Time: $formattedTime • Reels: $totalReels • Pickups: $totalPickups")
                .setSmallIcon(android.R.drawable.ic_menu_today)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(6001, notification)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
