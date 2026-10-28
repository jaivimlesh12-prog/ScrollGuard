package com.scrollguard.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scrollguard.data.repository.GameRepository
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.ScreenTimeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit

/**
 * Worker that purges detailed history records older than 90 days to prevent excessive DB growth.
 */
class DataCleanupWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CleanupEntryPoint {
        fun screenTimeRepository(): ScreenTimeRepository
        fun reelRepository(): ReelRepository
        fun gameRepository(): GameRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                CleanupEntryPoint::class.java
            )

            val ninetyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(90)
            entryPoint.screenTimeRepository().cleanupOldData(ninetyDaysAgo)
            entryPoint.reelRepository().cleanupOldData(ninetyDaysAgo)
            entryPoint.gameRepository().cleanupOldData(ninetyDaysAgo)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
