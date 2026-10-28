package com.scrollguard.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Logs each phone pickup (screen-on / unlock) event.
 *
 * [timestamp] is the exact epoch-millis of the event, while [date] is the
 * start-of-day epoch used for daily aggregation queries.
 */
@Entity(tableName = "phone_pickups")
data class PhonePickupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "date") val date: Long
)
