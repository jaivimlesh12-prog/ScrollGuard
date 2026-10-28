package com.scrollguard.data.repository

import com.scrollguard.data.db.dao.GameSessionDao
import com.scrollguard.data.db.entity.GameSessionEntity
import com.scrollguard.util.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameSessionDao: GameSessionDao
) {
    fun getSessionsForToday(): Flow<List<GameSessionEntity>> =
        gameSessionDao.getSessionsByDate(DateUtils.getStartOfDay())

    fun getTotalGameTimeToday(): Flow<Long?> =
        gameSessionDao.getTotalGameTimeForDate(DateUtils.getStartOfDay())

    fun getGameSessionCountToday(): Flow<Int?> =
        gameSessionDao.getGameSessionCountForDate(DateUtils.getStartOfDay())

    fun getMostPlayedGame(startDate: Long, endDate: Long): Flow<GameSessionEntity?> =
        gameSessionDao.getMostPlayedGame(startDate, endDate)

    suspend fun recordGameSession(
        packageName: String,
        gameName: String,
        category: String,
        startTime: Long,
        endTime: Long
    ) {
        val duration = (endTime - startTime).coerceAtLeast(0)
        val entity = GameSessionEntity(
            packageName = packageName,
            gameName = gameName,
            gameCategory = category,
            startTime = startTime,
            endTime = endTime,
            durationMs = duration,
            date = DateUtils.getStartOfDay(startTime)
        )
        gameSessionDao.insertSession(entity)
    }

    suspend fun cleanupOldData(beforeDate: Long) {
        gameSessionDao.deleteOlderThan(beforeDate)
    }
}
