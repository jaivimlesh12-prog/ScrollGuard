package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrollguard.data.db.entity.UsageScreenshotEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [UsageScreenshotEntity].
 *
 * Supports range-based retrieval for gallery views and pruning of old
 * screenshot metadata.
 */
@Dao
interface UsageScreenshotDao {

    /** Inserts a screenshot metadata record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenshot(entity: UsageScreenshotEntity)

    /** Returns all screenshot records within the inclusive timestamp range. */
    @Query("SELECT * FROM usage_screenshots WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getScreenshotsByDateRange(startDate: Long, endDate: Long): Flow<List<UsageScreenshotEntity>>

    /** Deletes all records with a timestamp strictly before [date]. */
    @Query("DELETE FROM usage_screenshots WHERE timestamp < :date")
    suspend fun deleteOlderThan(date: Long)

    /** Returns the total number of stored screenshot records. */
    @Query("SELECT COUNT(*) FROM usage_screenshots")
    fun getScreenshotCount(): Flow<Int?>
}
