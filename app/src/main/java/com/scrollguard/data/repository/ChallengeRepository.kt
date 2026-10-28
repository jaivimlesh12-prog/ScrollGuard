package com.scrollguard.data.repository

import com.scrollguard.data.db.dao.ChallengeDao
import com.scrollguard.data.db.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val challengeDao: ChallengeDao
) {
    fun getActiveChallenges(): Flow<List<ChallengeEntity>> =
        challengeDao.getActiveChallenges()

    fun getCompletedChallenges(): Flow<List<ChallengeEntity>> =
        challengeDao.getCompletedChallenges()

    fun getAllChallenges(): Flow<List<ChallengeEntity>> =
        challengeDao.getAllChallenges()

    suspend fun createChallenge(
        title: String,
        description: String,
        type: String,
        targetType: String,
        targetValue: Long,
        startDate: Long,
        endDate: Long
    ) {
        val entity = ChallengeEntity(
            title = title,
            description = description,
            type = type,
            targetType = targetType,
            targetValue = targetValue,
            startDate = startDate,
            endDate = endDate
        )
        challengeDao.insertChallenge(entity)
    }

    suspend fun updateProgress(id: Long, currentValue: Long) {
        // Handled via DAO update
    }

    suspend fun deleteChallenge(entity: ChallengeEntity) =
        challengeDao.deleteChallenge(entity)
}
