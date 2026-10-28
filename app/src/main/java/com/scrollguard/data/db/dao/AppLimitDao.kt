package com.scrollguard.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.scrollguard.data.db.entity.AppLimitEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [AppLimitEntity].
 *
 * Provides both reactive [Flow]-based queries for the UI layer and
 * a synchronous suspend query ([getLimitByPackageSync]) for use in
 * background services that need an immediate result.
 */
@Dao
interface AppLimitDao {

    /** Inserts an app-limit record; replaces on primary-key conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimit(entity: AppLimitEntity)

    /** Updates an existing app-limit record. */
    @Update
    suspend fun updateLimit(entity: AppLimitEntity)

    /** Deletes the app-limit record with the given [id]. */
    @Query("DELETE FROM app_limits WHERE id = :id")
    suspend fun deleteLimitById(id: Long)

    /** Returns all configured app limits. */
    @Query("SELECT * FROM app_limits")
    fun getAllLimits(): Flow<List<AppLimitEntity>>

    /** Returns only the limits that are currently enabled. */
    @Query("SELECT * FROM app_limits WHERE is_enabled = 1")
    fun getEnabledLimits(): Flow<List<AppLimitEntity>>

    /** Returns the limit for a specific package, or null if none exists. */
    @Query("SELECT * FROM app_limits WHERE package_name = :packageName LIMIT 1")
    fun getLimitByPackage(packageName: String): Flow<AppLimitEntity?>

    /**
     * Synchronous (suspend) lookup for use in services / workers that
     * need an immediate, non-reactive result.
     */
    @Query("SELECT * FROM app_limits WHERE package_name = :packageName LIMIT 1")
    suspend fun getLimitByPackageSync(packageName: String): AppLimitEntity?
}
