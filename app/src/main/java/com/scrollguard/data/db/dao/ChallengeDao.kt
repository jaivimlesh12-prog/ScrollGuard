package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.scrollguard.data.db.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [ChallengeEntity].
 *
 * Provides queries filtered by active/completed state and full CRUD
 * operations for managing user challenges.
 */
@Dao
interface ChallengeDao {

    /** Inserts a challenge record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(entity: ChallengeEntity)

    /** Updates an existing challenge record. */
    @Update
    suspend fun updateChallenge(entity: ChallengeEntity)

    /** Returns only challenges that are currently active (not completed). */
    @Query("SELECT * FROM challenges WHERE is_active = 1 AND is_completed = 0")
    fun getActiveChallenges(): Flow<List<ChallengeEntity>>

    /** Returns only challenges that have been completed. */
    @Query("SELECT * FROM challenges WHERE is_completed = 1")
    fun getCompletedChallenges(): Flow<List<ChallengeEntity>>

    /** Returns all challenges regardless of state. */
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    /** Returns a single challenge by its [id], or null if not found. */
    @Query("SELECT * FROM challenges WHERE id = :id")
    fun getChallengeById(id: Long): Flow<ChallengeEntity?>

    /** Deletes the given challenge. */
    @Delete
    suspend fun deleteChallenge(entity: ChallengeEntity)
}
