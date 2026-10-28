package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Records per-app screen time usage aggregated by day.
 *
 * Stores usage duration in milliseconds, launch count, and categorises each app
 * (e.g. "Social Media", "Games", "Productivity") for grouped analytics.
 */
@Entity(tableName = "screen_time")
data class ScreenTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "usage_ms") val usageMs: Long,
    @ColumnInfo(name = "launch_count") val launchCount: Int,
    @ColumnInfo(name = "date") val date: Long
)
