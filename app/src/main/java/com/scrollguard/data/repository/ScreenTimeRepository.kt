package com.scrollguard.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import com.scrollguard.data.db.dao.CategoryUsage
import com.scrollguard.data.db.dao.PhonePickupDao
import com.scrollguard.data.db.dao.ScreenTimeDao
import com.scrollguard.data.db.entity.PhonePickupEntity
import com.scrollguard.data.db.entity.ScreenTimeEntity
import com.scrollguard.util.AppCategoryResolver
import com.scrollguard.util.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTimeRepository @Inject constructor(
    private val screenTimeDao: ScreenTimeDao,
    private val phonePickupDao: PhonePickupDao,
    private val usageStatsManager: UsageStatsManager,
    @ApplicationContext private val context: Context
) {
    fun getScreenTimeForToday(): Flow<List<ScreenTimeEntity>> =
        screenTimeDao.getScreenTimeByDate(DateUtils.getStartOfDay())

    fun getTotalScreenTimeToday(): Flow<Long?> =
        screenTimeDao.getTotalScreenTimeForDate(DateUtils.getStartOfDay())

    fun getTopAppsToday(limit: Int = 5): Flow<List<ScreenTimeEntity>> =
        screenTimeDao.getTopAppsByDate(DateUtils.getStartOfDay(), limit)

    fun getScreenTimeByCategory(): Flow<List<CategoryUsage>> =
        screenTimeDao.getScreenTimeByCategory(DateUtils.getStartOfDay())

    fun getTotalLaunchCountToday(): Flow<Int?> =
        screenTimeDao.getTotalLaunchCountForDate(DateUtils.getStartOfDay())

    fun getPickupCountToday(): Flow<Int?> =
        phonePickupDao.getPickupCountForDate(DateUtils.getStartOfDay())

    suspend fun recordPickup(timestamp: Long = System.currentTimeMillis()) {
        val entity = PhonePickupEntity(
            timestamp = timestamp,
            date = DateUtils.getStartOfDay(timestamp)
        )
        phonePickupDao.insertPickup(entity)
    }

    suspend fun syncUsageStats() = withContext(Dispatchers.IO) {
        val startOfDay = DateUtils.getStartOfDay()
        val now = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            now
        ) ?: return@withContext

        val pm = context.packageManager

        for (usage in stats) {
            if (usage.totalTimeInForeground <= 0) continue

            val packageName = usage.packageName
            val appName = try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName
            }

            val category = AppCategoryResolver.getCategory(packageName)

            val entity = ScreenTimeEntity(
                packageName = packageName,
                appName = appName,
                category = category,
                usageMs = usage.totalTimeInForeground,
                launchCount = 0,
                date = startOfDay
            )
            screenTimeDao.upsertScreenTime(entity)
        }
    }

    suspend fun cleanupOldData(beforeDate: Long) {
        screenTimeDao.deleteOlderThan(beforeDate)
        phonePickupDao.deleteOlderThan(beforeDate)
    }
}
