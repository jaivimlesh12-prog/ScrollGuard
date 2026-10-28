package com.scrollguard.ui.screen.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.db.entity.ScreenTimeEntity
import com.scrollguard.data.repository.GameRepository
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.ScreenTimeRepository
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.service.EyeStrainReminderService
import com.scrollguard.service.FloatingCounterService
import com.scrollguard.service.ScreenTimeService
import com.scrollguard.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalScreenTimeMs: Long = 0L,
    val totalReels: Int = 0,
    val reelLimit: Int = 50,
    val totalPickups: Int = 0,
    val totalGameTimeMs: Long = 0L,
    val topApps: List<ScreenTimeEntity> = emptyList(),
    val isFocusModeActive: Boolean = false,
    val isFloatingOverlayActive: Boolean = true,
    val streakDays: Int = 5
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val screenTimeRepository: ScreenTimeRepository,
    private val reelRepository: ReelRepository,
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private data class BaseStats(
        val totalTime: Long,
        val reels: Int,
        val reelLimit: Int,
        val pickups: Int
    )

    private data class ExtraStats(
        val gameTime: Long,
        val topApps: List<ScreenTimeEntity>,
        val focusActive: Boolean
    )

    private fun loadDashboardData() {
        viewModelScope.launch {
            val baseStatsFlow = combine(
                screenTimeRepository.getTotalScreenTimeToday(),
                reelRepository.getTotalReelsToday(),
                settingsRepository.dailyReelLimit,
                screenTimeRepository.getPickupCountToday()
            ) { totalTime, reels, reelLimit, pickups ->
                BaseStats(
                    totalTime = totalTime ?: 0L,
                    reels = reels ?: 0,
                    reelLimit = reelLimit,
                    pickups = pickups ?: 0
                )
            }

            val extraStatsFlow = combine(
                gameRepository.getTotalGameTimeToday(),
                screenTimeRepository.getTopAppsToday(5),
                settingsRepository.isFocusModeActive
            ) { gameTime, topApps, focusActive ->
                ExtraStats(
                    gameTime = gameTime ?: 0L,
                    topApps = topApps,
                    focusActive = focusActive
                )
            }

            combine(baseStatsFlow, extraStatsFlow) { base, extra ->
                DashboardUiState(
                    totalScreenTimeMs = base.totalTime,
                    totalReels = base.reels,
                    reelLimit = base.reelLimit,
                    totalPickups = base.pickups,
                    totalGameTimeMs = extra.gameTime,
                    topApps = extra.topApps,
                    isFocusModeActive = extra.focusActive,
                    isFloatingOverlayActive = true,
                    streakDays = 5
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun toggleFocusMode() {
        viewModelScope.launch {
            val current = _uiState.value.isFocusModeActive
            settingsRepository.setFocusModeActive(!current)
        }
    }

    fun manualIncrementReel() {
        viewModelScope.launch {
            reelRepository.recordReelScroll(
                packageName = Constants.INSTAGRAM_PACKAGE,
                source = "manual",
                browserPackage = null
            )
        }
    }

    fun startServices(context: Context) {
        // Start ScreenTimeService
        val screenTimeIntent = Intent(context, ScreenTimeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(screenTimeIntent)
        } else {
            context.startService(screenTimeIntent)
        }

        // Start FloatingCounterService
        val overlayIntent = Intent(context, FloatingCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(overlayIntent)
        } else {
            context.startService(overlayIntent)
        }

        // Start EyeStrainReminderService
        val eyeStrainIntent = Intent(context, EyeStrainReminderService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(eyeStrainIntent)
        } else {
            context.startService(eyeStrainIntent)
        }
    }
}
