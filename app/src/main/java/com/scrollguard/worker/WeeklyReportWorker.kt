package com.scrollguard.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
 * Worker that calculates weekly usage comparisons and notifies the user.
 */
class WeeklyReportWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeeklyReportEntryPoint {
        fun screenTimeRepository(): ScreenTimeRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WeeklyReportEntryPoint::class.java
            )
            val screenTimeRepo = entryPoint.screenTimeRepository()
            val totalToday = screenTimeRepo.getTotalScreenTimeToday().first() ?: 0L

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, Constants.CHANNEL_DAILY)
                .setContentTitle("📈 Your Weekly Screen Time Summary")
                .setContentText("Check out your weekly screen time trends and reel habits!")
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(6002, notification)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
