package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores a daily addiction score (0–100) computed per app.
 *
 * Higher scores indicate more addictive usage patterns. The score is
 * derived from a combination of screen time, reel count, launch frequency,
 * and pickup correlation.
 */
@Entity(tableName = "addiction_scores")
data class AddictionScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "score") val score: Int,
    @ColumnInfo(name = "date") val date: Long
)
