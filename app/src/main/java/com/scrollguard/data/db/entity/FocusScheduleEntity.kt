package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines a recurring focus schedule that blocks a set of apps during specific hours.
 *
 * [blockedApps] is a JSON-encoded string array of package names.
 * [daysOfWeek] is a JSON-encoded int array where 1 = Monday … 7 = Sunday.
 */
@Entity(tableName = "focus_schedules")
data class FocusScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "blocked_apps") val blockedApps: String,
    @ColumnInfo(name = "start_hour") val startHour: Int,
    @ColumnInfo(name = "start_minute") val startMinute: Int,
    @ColumnInfo(name = "end_hour") val endHour: Int,
    @ColumnInfo(name = "end_minute") val endMinute: Int,
    @ColumnInfo(name = "days_of_week") val daysOfWeek: String,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true
)
