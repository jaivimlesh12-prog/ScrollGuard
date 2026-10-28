package com.scrollguard.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.repository.GameRepository
import com.scrollguard.data.repository.ReelRepository
import com.scrollguard.data.repository.ScreenTimeRepository
import com.scrollguard.data.repository.SettingsRepository
import com.scrollguard.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val totalReels: Int = 0,
    val instagramReels: Int = 0,
    val youtubeShorts: Int = 0,
    val scrollDistanceKm: Float = 0f,
    val screenTimeMs: Long = 0L,
    val hourlyWage: Float = 0f,
    val wastedTimeCost: Float = 0f,
    val gameTimeMs: Long = 0L,
    val gameSessionsCount: Int = 0
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val reelRepository: ReelRepository,
    private val screenTimeRepository: ScreenTimeRepository,
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                reelRepository.getTotalReelsToday(),
                reelRepository.getTotalReelsByApp(Constants.INSTAGRAM_PACKAGE),
                reelRepository.getTotalReelsByApp(Constants.YOUTUBE_PACKAGE),
                screenTimeRepository.getTotalScreenTimeToday(),
                settingsRepository.hourlyWage,
                gameRepository.getTotalGameTimeToday(),
                gameRepository.getGameSessionCountToday()
            ) { reels, igReels, ytShorts, screenTime, wage, gameTime, gameSessions ->
                val totalR = reels ?: 0
                // Calculate scroll distance: each reel is approx 2400px @ 440 dpi ~= 0.138 meters
                val distanceMeters = totalR * 0.15f
                val distanceKm = distanceMeters / 1000f

                val timeHours = (screenTime ?: 0L) / (1000f * 60 * 60)
                val cost = timeHours * wage

                AnalyticsUiState(
                    totalReels = totalR,
                    instagramReels = igReels ?: 0,
                    youtubeShorts = ytShorts ?: 0,
                    scrollDistanceKm = distanceKm,
                    screenTimeMs = screenTime ?: 0L,
                    hourlyWage = wage,
                    wastedTimeCost = cost,
                    gameTimeMs = gameTime ?: 0L,
                    gameSessionsCount = gameSessions ?: 0
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}
