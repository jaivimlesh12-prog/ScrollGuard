package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrollguard.data.db.entity.ReelCountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Summary projection for daily reel totals.
 */
data class DailyReelSummary(
    val date: Long,
    val totalCount: Int
)

/**
 * Data access object for [ReelCountEntity].
 *
 * Provides reactive queries via [Flow] for UI consumption and
 * suspend functions for background write/delete operations.
 */
@Dao
interface ReelCountDao {

    /** Inserts a reel-count record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReelCount(entity: ReelCountEntity)

    /** Returns all reel-count records for the given [date]. */
    @Query("SELECT * FROM reel_counts WHERE date = :date")
    fun getReelCountsByDate(date: Long): Flow<List<ReelCountEntity>>

    /** Returns all reel-count records within the inclusive date range. */
    @Query("SELECT * FROM reel_counts WHERE date BETWEEN :startDate AND :endDate")
    fun getReelCountsByDateRange(startDate: Long, endDate: Long): Flow<List<ReelCountEntity>>

    /** Returns the sum of all reel counts on the given [date]. */
    @Query("SELECT SUM(count) FROM reel_counts WHERE date = :date")
    fun getTotalReelCountForDate(date: Long): Flow<Int?>

    /** Returns the sum of reel counts for a specific app on the given [date]. */
    @Query("SELECT SUM(count) FROM reel_counts WHERE package_name = :packageName AND date = :date")
    fun getTotalReelCountByAppForDate(packageName: String, date: Long): Flow<Int?>

    /** Returns daily reel totals aggregated across all apps for the given range. */
    @Query(
        """
        SELECT date, SUM(count) AS totalCount
        FROM reel_counts
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
        """
    )
    fun getDailyReelCounts(startDate: Long, endDate: Long): Flow<List<DailyReelSummary>>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM reel_counts WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
