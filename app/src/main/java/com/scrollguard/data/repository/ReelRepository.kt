package com.scrollguard.data.repository

import com.scrollguard.data.db.dao.DailyReelSummary
import com.scrollguard.data.db.dao.ReelCountDao
import com.scrollguard.data.db.entity.ReelCountEntity
import com.scrollguard.util.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReelRepository @Inject constructor(
    private val reelCountDao: ReelCountDao
) {
    fun getReelCountsForToday(): Flow<List<ReelCountEntity>> =
        reelCountDao.getReelCountsByDate(DateUtils.getStartOfDay())

    fun getTotalReelsToday(): Flow<Int?> =
        reelCountDao.getTotalReelCountForDate(DateUtils.getStartOfDay())

    fun getTotalReelsByApp(packageName: String): Flow<Int?> =
        reelCountDao.getTotalReelCountByAppForDate(packageName, DateUtils.getStartOfDay())

    fun getDailyReelCounts(startDate: Long, endDate: Long): Flow<List<DailyReelSummary>> =
        reelCountDao.getDailyReelCounts(startDate, endDate)

    suspend fun recordReelScroll(packageName: String, source: String, browserPackage: String?) {
        val entity = ReelCountEntity(
            packageName = packageName,
            source = source,
            browserPackage = browserPackage,
            count = 1,
            date = DateUtils.getStartOfDay()
        )
        reelCountDao.insertReelCount(entity)
    }

    suspend fun cleanupOldData(beforeDate: Long) {
        reelCountDao.deleteOlderThan(beforeDate)
    }
}
