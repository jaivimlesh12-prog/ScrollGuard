package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrollguard.data.db.entity.AddictionScoreEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [AddictionScoreEntity].
 *
 * Provides queries for per-date scores, date-range analysis, per-app
 * lookups, and a "most addictive apps" ranking.
 */
@Dao
interface AddictionScoreDao {

    /** Inserts an addiction-score record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(entity: AddictionScoreEntity)

    /** Returns all addiction scores for the given [date]. */
    @Query("SELECT * FROM addiction_scores WHERE date = :date")
    fun getScoresByDate(date: Long): Flow<List<AddictionScoreEntity>>

    /** Returns all addiction scores within the inclusive date range. */
    @Query("SELECT * FROM addiction_scores WHERE date BETWEEN :startDate AND :endDate")
    fun getScoresByDateRange(startDate: Long, endDate: Long): Flow<List<AddictionScoreEntity>>

    /** Returns the addiction score for a specific app on the given [date]. */
    @Query("SELECT * FROM addiction_scores WHERE package_name = :packageName AND date = :date LIMIT 1")
    fun getScoreByApp(packageName: String, date: Long): Flow<AddictionScoreEntity?>

    /** Returns the top [limit] most addictive apps on the given [date]. */
    @Query("SELECT * FROM addiction_scores WHERE date = :date ORDER BY score DESC LIMIT :limit")
    fun getTopAddictiveApps(date: Long, limit: Int): Flow<List<AddictionScoreEntity>>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM addiction_scores WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
