package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.scrollguard.data.db.entity.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [NotificationLogEntity].
 *
 * Supports upsert semantics so that repeated notifications from the same
 * app on the same day increment a single row's count rather than creating
 * duplicates.
 */
@Dao
interface NotificationLogDao {

    /** Inserts a notification-log record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entity: NotificationLogEntity)

    /** Inserts or updates a notification-log record using Room's built-in upsert. */
    @Upsert
    suspend fun upsertLog(entity: NotificationLogEntity)

    /** Returns all notification logs for the given [date]. */
    @Query("SELECT * FROM notification_logs WHERE date = :date")
    fun getLogsByDate(date: Long): Flow<List<NotificationLogEntity>>

    /** Returns the total number of notifications across all apps on [date]. */
    @Query("SELECT SUM(count) FROM notification_logs WHERE date = :date")
    fun getTotalNotificationsForDate(date: Long): Flow<Int?>

    /** Returns the top [limit] noisiest apps on the given [date]. */
    @Query("SELECT * FROM notification_logs WHERE date = :date ORDER BY count DESC LIMIT :limit")
    fun getTopNoisyApps(date: Long, limit: Int): Flow<List<NotificationLogEntity>>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM notification_logs WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
