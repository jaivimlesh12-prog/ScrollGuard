package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scrollguard.data.db.entity.ScreenTimeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Summary projection for per-category usage totals.
 */
data class CategoryUsage(
    val category: String,
    val totalUsageMs: Long
)

/**
 * Data access object for [ScreenTimeEntity].
 *
 * Supports both insert and upsert semantics, reactive list queries,
 * and aggregate analytics (top apps, category breakdown, totals).
 */
@Dao
interface ScreenTimeDao {

    /** Inserts a screen-time record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenTime(entity: ScreenTimeEntity)

    /** Inserts or updates a screen-time record using Room's built-in upsert. */
    @Upsert
    suspend fun upsertScreenTime(entity: ScreenTimeEntity)

    /** Returns all screen-time records for the given [date]. */
    @Query("SELECT * FROM screen_time WHERE date = :date")
    fun getScreenTimeByDate(date: Long): Flow<List<ScreenTimeEntity>>

    /** Returns all screen-time records within the inclusive date range. */
    @Query("SELECT * FROM screen_time WHERE date BETWEEN :startDate AND :endDate")
    fun getScreenTimeByDateRange(startDate: Long, endDate: Long): Flow<List<ScreenTimeEntity>>

    /** Returns the total usage in milliseconds for the given [date]. */
    @Query("SELECT SUM(usage_ms) FROM screen_time WHERE date = :date")
    fun getTotalScreenTimeForDate(date: Long): Flow<Long?>

    /** Returns the top [limit] apps by usage on the given [date], descending. */
    @Query("SELECT * FROM screen_time WHERE date = :date ORDER BY usage_ms DESC LIMIT :limit")
    fun getTopAppsByDate(date: Long, limit: Int): Flow<List<ScreenTimeEntity>>

    /** Returns aggregated usage per category for the given [date]. */
    @Query(
        """
        SELECT category, SUM(usage_ms) AS totalUsageMs
        FROM screen_time
        WHERE date = :date
        GROUP BY category
        ORDER BY totalUsageMs DESC
        """
    )
    fun getScreenTimeByCategory(date: Long): Flow<List<CategoryUsage>>

    /** Returns the total number of app launches on the given [date]. */
    @Query("SELECT SUM(launch_count) FROM screen_time WHERE date = :date")
    fun getTotalLaunchCountForDate(date: Long): Flow<Int?>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM screen_time WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
