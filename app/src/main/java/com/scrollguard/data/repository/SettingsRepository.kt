package com.scrollguard.data.repository

import com.scrollguard.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val userPreferences: UserPreferences
) {
    val hourlyWage: Flow<Float> = userPreferences.hourlyWage
    val bedtimeHour: Flow<Int> = userPreferences.bedtimeHour
    val bedtimeMinute: Flow<Int> = userPreferences.bedtimeMinute
    val isBedtimeModeEnabled: Flow<Boolean> = userPreferences.isBedtimeModeEnabled
    val isEyeStrainEnabled: Flow<Boolean> = userPreferences.isEyeStrainEnabled
    val eyeStrainIntervalMin: Flow<Int> = userPreferences.eyeStrainIntervalMin
    val breakDurationSec: Flow<Int> = userPreferences.breakDurationSec
    val themeMode: Flow<Int> = userPreferences.themeMode
    val dailyPickupLimit: Flow<Int> = userPreferences.dailyPickupLimit
    val dailyReelLimit: Flow<Int> = userPreferences.dailyReelLimit
    val isOnboardingComplete: Flow<Boolean> = userPreferences.isOnboardingComplete
    val isFocusModeActive: Flow<Boolean> = userPreferences.isFocusModeActive
    val whitelistedApps: Flow<Set<String>> = userPreferences.whitelistedApps

    suspend fun setHourlyWage(wage: Float) = userPreferences.setHourlyWage(wage)
    suspend fun setBedtime(hour: Int, minute: Int) = userPreferences.setBedtime(hour, minute)
    suspend fun setBedtimeModeEnabled(enabled: Boolean) = userPreferences.setBedtimeModeEnabled(enabled)
    suspend fun setEyeStrainEnabled(enabled: Boolean) = userPreferences.setEyeStrainEnabled(enabled)
    suspend fun setEyeStrainInterval(intervalMin: Int, breakSec: Int) = userPreferences.setEyeStrainInterval(intervalMin, breakSec)
    suspend fun setThemeMode(mode: Int) = userPreferences.setThemeMode(mode)
    suspend fun setDailyPickupLimit(limit: Int) = userPreferences.setDailyPickupLimit(limit)
    suspend fun setDailyReelLimit(limit: Int) = userPreferences.setDailyReelLimit(limit)
    suspend fun setOnboardingComplete(completed: Boolean) = userPreferences.setOnboardingComplete(completed)
    suspend fun setFocusModeActive(active: Boolean) = userPreferences.setFocusModeActive(active)
    suspend fun setWhitelistedApps(packages: Set<String>) = userPreferences.setWhitelistedApps(packages)
}
