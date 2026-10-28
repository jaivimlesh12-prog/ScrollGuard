package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user challenge (daily, weekly, or monthly) to reduce usage.
 *
 * [targetType] specifies the metric being tracked ("screentime", "reels",
 * "noapp", "pickups") and [targetValue] the goal the user aims to stay
 * under (or reach). Progress is tracked via [currentValue].
 */
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "target_type") val targetType: String,
    @ColumnInfo(name = "target_value") val targetValue: Long,
    @ColumnInfo(name = "current_value") val currentValue: Long = 0,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
