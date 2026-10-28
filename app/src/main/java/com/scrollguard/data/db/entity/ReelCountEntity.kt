package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks the number of reels/short-form videos viewed per app per day.
 *
 * Each row represents a reel-view event batch for a specific app on a given date.
 * The [source] field distinguishes between in-app viewing and browser-based viewing.
 */
@Entity(tableName = "reel_counts")
data class ReelCountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "browser_package") val browserPackage: String? = null,
    @ColumnInfo(name = "count") val count: Int = 1,
    @ColumnInfo(name = "date") val date: Long
)
