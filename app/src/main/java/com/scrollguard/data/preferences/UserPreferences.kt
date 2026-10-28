package com.scrollguard.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrollguard_settings")

/**
 * DataStore-backed persistent user preferences for ScrollGuard.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val HOURLY_WAGE = floatPreferencesKey("hourly_wage")
        val BEDTIME_HOUR = intPreferencesKey("bedtime_hour")
        val BEDTIME_MINUTE = intPreferencesKey("bedtime_minute")
        val IS_BEDTIME_MODE_ENABLED = booleanPreferencesKey("is_bedtime_mode_enabled")
        val IS_EYE_STRAIN_ENABLED = booleanPreferencesKey("is_eye_strain_enabled")
        val EYE_STRAIN_INTERVAL_MIN = intPreferencesKey("eye_strain_interval_min")
        val BREAK_DURATION_SEC = intPreferencesKey("break_duration_sec")
        val THEME_MODE = intPreferencesKey("theme_mode") // 0 = System, 1 = Light, 2 = Dark
        val DAILY_PICKUP_LIMIT = intPreferencesKey("daily_pickup_limit")
        val DAILY_REEL_LIMIT = intPreferencesKey("daily_reel_limit")
        val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        val IS_FOCUS_MODE_ACTIVE = booleanPreferencesKey("is_focus_mode_active")
        val WHITELISTED_APPS = stringSetPreferencesKey("whitelisted_apps")
    }

    val hourlyWage: Flow<Float> = dataStore.data.map { it[HOURLY_WAGE] ?: 0f }
    val bedtimeHour: Flow<Int> = dataStore.data.map { it[BEDTIME_HOUR] ?: 23 }
    val bedtimeMinute: Flow<Int> = dataStore.data.map { it[BEDTIME_MINUTE] ?: 0 }
    val isBedtimeModeEnabled: Flow<Boolean> = dataStore.data.map { it[IS_BEDTIME_MODE_ENABLED] ?: false }
    val isEyeStrainEnabled: Flow<Boolean> = dataStore.data.map { it[IS_EYE_STRAIN_ENABLED] ?: true }
    val eyeStrainIntervalMin: Flow<Int> = dataStore.data.map { it[EYE_STRAIN_INTERVAL_MIN] ?: 20 }
    val breakDurationSec: Flow<Int> = dataStore.data.map { it[BREAK_DURATION_SEC] ?: 20 }
    val themeMode: Flow<Int> = dataStore.data.map { it[THEME_MODE] ?: 0 }
    val dailyPickupLimit: Flow<Int> = dataStore.data.map { it[DAILY_PICKUP_LIMIT] ?: 100 }
    val dailyReelLimit: Flow<Int> = dataStore.data.map { it[DAILY_REEL_LIMIT] ?: 50 }
    val isOnboardingComplete: Flow<Boolean> = dataStore.data.map { it[IS_ONBOARDING_COMPLETE] ?: false }
    val isFocusModeActive: Flow<Boolean> = dataStore.data.map { it[IS_FOCUS_MODE_ACTIVE] ?: false }
    val whitelistedApps: Flow<Set<String>> = dataStore.data.map {
        it[WHITELISTED_APPS] ?: setOf(
            "com.google.android.dialer",
            "com.android.dialer",
            "com.google.android.apps.messaging",
            "com.android.mms"
        )
    }

    suspend fun setHourlyWage(wage: Float) {
        dataStore.edit { it[HOURLY_WAGE] = wage }
    }

    suspend fun setBedtime(hour: Int, minute: Int) {
        dataStore.edit {
            it[BEDTIME_HOUR] = hour
            it[BEDTIME_MINUTE] = minute
        }
    }

    suspend fun setBedtimeModeEnabled(enabled: Boolean) {
        dataStore.edit { it[IS_BEDTIME_MODE_ENABLED] = enabled }
    }

    suspend fun setEyeStrainEnabled(enabled: Boolean) {
        dataStore.edit { it[IS_EYE_STRAIN_ENABLED] = enabled }
    }

    suspend fun setEyeStrainInterval(intervalMin: Int, breakSec: Int) {
        dataStore.edit {
            it[EYE_STRAIN_INTERVAL_MIN] = intervalMin
            it[BREAK_DURATION_SEC] = breakSec
        }
    }

    suspend fun setThemeMode(mode: Int) {
        dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setDailyPickupLimit(limit: Int) {
        dataStore.edit { it[DAILY_PICKUP_LIMIT] = limit }
    }

    suspend fun setDailyReelLimit(limit: Int) {
        dataStore.edit { it[DAILY_REEL_LIMIT] = limit }
    }

    suspend fun setOnboardingComplete(completed: Boolean) {
        dataStore.edit { it[IS_ONBOARDING_COMPLETE] = completed }
    }

    suspend fun setFocusModeActive(active: Boolean) {
        dataStore.edit { it[IS_FOCUS_MODE_ACTIVE] = active }
    }

    suspend fun setWhitelistedApps(packages: Set<String>) {
        dataStore.edit { it[WHITELISTED_APPS] = packages }
    }
}
