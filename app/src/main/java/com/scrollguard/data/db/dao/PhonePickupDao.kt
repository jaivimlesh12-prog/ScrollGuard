package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrollguard.data.db.entity.PhonePickupEntity
import kotlinx.coroutines.flow.Flow

/**
 * Summary projection for daily phone-pickup counts.
 */
data class DailyPickupSummary(
    val date: Long,
    val count: Int
)

/**
 * Data access object for [PhonePickupEntity].
 *
 * Provides per-date counts, range-based listing, and daily aggregation
 * for trend charts.
 */
@Dao
interface PhonePickupDao {

    /** Inserts a phone-pickup record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickup(entity: PhonePickupEntity)

    /** Returns the total number of pickups on the given [date]. */
    @Query("SELECT COUNT(*) FROM phone_pickups WHERE date = :date")
    fun getPickupCountForDate(date: Long): Flow<Int?>

    /** Returns all pickup records within the inclusive date range. */
    @Query("SELECT * FROM phone_pickups WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    fun getPickupsByDateRange(startDate: Long, endDate: Long): Flow<List<PhonePickupEntity>>

    /** Returns daily pickup counts aggregated by date for trend analysis. */
    @Query(
        """
        SELECT date, COUNT(*) AS count
        FROM phone_pickups
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
        """
    )
    fun getDailyPickupCounts(startDate: Long, endDate: Long): Flow<List<DailyPickupSummary>>

    /** Deletes all records with a date strictly before [date]. */
    @Query("DELETE FROM phone_pickups WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}
