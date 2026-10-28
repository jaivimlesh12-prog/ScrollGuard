package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.scrollguard.data.db.entity.FocusScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [FocusScheduleEntity].
 *
 * Provides full CRUD operations and filtered queries for enabled
 * schedules used by the focus-mode enforcement service.
 */
@Dao
interface FocusScheduleDao {

    /** Inserts a focus-schedule record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(entity: FocusScheduleEntity)

    /** Updates an existing focus-schedule record. */
    @Update
    suspend fun updateSchedule(entity: FocusScheduleEntity)

    /** Deletes the given focus-schedule. */
    @Delete
    suspend fun deleteSchedule(entity: FocusScheduleEntity)

    /** Returns all focus schedules. */
    @Query("SELECT * FROM focus_schedules")
    fun getAllSchedules(): Flow<List<FocusScheduleEntity>>

    /** Returns only the schedules that are currently enabled. */
    @Query("SELECT * FROM focus_schedules WHERE is_enabled = 1")
    fun getEnabledSchedules(): Flow<List<FocusScheduleEntity>>

    /** Returns a single schedule by its [id], or null if not found. */
    @Query("SELECT * FROM focus_schedules WHERE id = :id")
    fun getScheduleById(id: Long): Flow<FocusScheduleEntity?>
}
