package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines user-configured usage limits for individual apps.
 *
 * Limits can be set on daily screen time, reel count, or launch count.
 * When [isStrict] is true the app is blocked without the option to extend.
 * Otherwise the user may extend up to [maxExtensions] times per day.
 */
@Entity(tableName = "app_limits")
data class AppLimitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "daily_limit_ms") val dailyLimitMs: Long = 0,
    @ColumnInfo(name = "daily_reel_limit") val dailyReelLimit: Int = 0,
    @ColumnInfo(name = "daily_launch_limit") val dailyLaunchLimit: Int = 0,
    @ColumnInfo(name = "is_strict") val isStrict: Boolean = false,
    @ColumnInfo(name = "max_extensions") val maxExtensions: Int = 3,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true
)
