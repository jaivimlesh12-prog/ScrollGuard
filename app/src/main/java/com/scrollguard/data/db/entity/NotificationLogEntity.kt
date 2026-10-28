package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Aggregates notification counts per app per day.
 *
 * Helps users identify "noisy" apps that may contribute to compulsive
 * screen pickups and attention fragmentation.
 */
@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "date") val date: Long
)
