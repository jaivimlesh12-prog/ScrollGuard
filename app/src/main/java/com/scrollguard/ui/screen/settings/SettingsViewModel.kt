package com.scrollguard.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val dailyReelLimit: Int = 50,
    val dailyPickupLimit: Int = 100,
    val hourlyWage: Float = 0f,
    val isBedtimeModeEnabled: Boolean = false,
    val bedtimeHour: Int = 23,
    val bedtimeMinute: Int = 0,
    val isEyeStrainEnabled: Boolean = true,
    val eyeStrainIntervalMin: Int = 20,
    val themeMode: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private data class LimitsAndWage(
        val reelLimit: Int,
        val pickupLimit: Int,
        val wage: Float
    )

    private data class BedtimeSettings(
        val bedtimeEnabled: Boolean,
        val bHour: Int,
        val bMin: Int
    )

    private data class EyeAndTheme(
        val eyeEnabled: Boolean,
        val eyeMin: Int,
        val theme: Int
    )

    init {
        viewModelScope.launch {
            val limitsFlow = combine(
                settingsRepository.dailyReelLimit,
                settingsRepository.dailyPickupLimit,
                settingsRepository.hourlyWage
            ) { reelLimit, pickupLimit, wage ->
                LimitsAndWage(reelLimit, pickupLimit, wage)
            }

            val bedtimeFlow = combine(
                settingsRepository.isBedtimeModeEnabled,
                settingsRepository.bedtimeHour,
                settingsRepository.bedtimeMinute
            ) { bedtimeEnabled, bHour, bMin ->
                BedtimeSettings(bedtimeEnabled, bHour, bMin)
            }

            val eyeAndThemeFlow = combine(
                settingsRepository.isEyeStrainEnabled,
                settingsRepository.eyeStrainIntervalMin,
                settingsRepository.themeMode
            ) { eyeEnabled, eyeMin, theme ->
                EyeAndTheme(eyeEnabled, eyeMin, theme)
            }

            combine(limitsFlow, bedtimeFlow, eyeAndThemeFlow) { limits, bedtime, eyeTheme ->
                SettingsUiState(
                    dailyReelLimit = limits.reelLimit,
                    dailyPickupLimit = limits.pickupLimit,
                    hourlyWage = limits.wage,
                    isBedtimeModeEnabled = bedtime.bedtimeEnabled,
                    bedtimeHour = bedtime.bHour,
                    bedtimeMinute = bedtime.bMin,
                    isEyeStrainEnabled = eyeTheme.eyeEnabled,
                    eyeStrainIntervalMin = eyeTheme.eyeMin,
                    themeMode = eyeTheme.theme
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setDailyReelLimit(limit: Int) {
        viewModelScope.launch { settingsRepository.setDailyReelLimit(limit) }
    }

    fun setDailyPickupLimit(limit: Int) {
        viewModelScope.launch { settingsRepository.setDailyPickupLimit(limit) }
    }

    fun setHourlyWage(wage: Float) {
        viewModelScope.launch { settingsRepository.setHourlyWage(wage) }
    }

    fun setBedtimeEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBedtimeModeEnabled(enabled) }
    }

    fun setEyeStrainEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setEyeStrainEnabled(enabled) }
    }
}
