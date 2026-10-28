package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Metadata for a captured usage screenshot stored on the file system.
 *
 * The actual image is at [filePath]; this entity enables querying
 * screenshots by date range or package and pruning old files.
 */
@Entity(tableName = "usage_screenshots")
data class UsageScreenshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
