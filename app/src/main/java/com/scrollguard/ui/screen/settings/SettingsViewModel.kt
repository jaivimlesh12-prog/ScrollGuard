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

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.dailyReelLimit,
                settingsRepository.dailyPickupLimit,
                settingsRepository.hourlyWage,
                settingsRepository.isBedtimeModeEnabled,
                settingsRepository.bedtimeHour,
                settingsRepository.bedtimeMinute,
                settingsRepository.isEyeStrainEnabled,
                settingsRepository.eyeStrainIntervalMin,
                settingsRepository.themeMode
            ) { reelLimit, pickupLimit, wage, bedtimeEnabled, bHour, bMin, eyeEnabled, eyeMin, theme ->
                SettingsUiState(
                    dailyReelLimit = reelLimit,
                    dailyPickupLimit = pickupLimit,
                    hourlyWage = wage,
                    isBedtimeModeEnabled = bedtimeEnabled,
                    bedtimeHour = bHour,
                    bedtimeMinute = bMin,
                    isEyeStrainEnabled = eyeEnabled,
                    eyeStrainIntervalMin = eyeMin,
                    themeMode = theme
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
