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

    private data class ReelStats(
        val totalReels: Int,
        val igReels: Int,
        val ytShorts: Int
    )

    private data class GeneralStats(
        val screenTime: Long,
        val wage: Float,
        val gameTime: Long,
        val gameSessions: Int
    )

    init {
        viewModelScope.launch {
            val reelStatsFlow = combine(
                reelRepository.getTotalReelsToday(),
                reelRepository.getTotalReelsByApp(Constants.INSTAGRAM_PACKAGE),
                reelRepository.getTotalReelsByApp(Constants.YOUTUBE_PACKAGE)
            ) { reels, igReels, ytShorts ->
                ReelStats(
                    totalReels = reels ?: 0,
                    igReels = igReels ?: 0,
                    ytShorts = ytShorts ?: 0
                )
            }

            val generalStatsFlow = combine(
                screenTimeRepository.getTotalScreenTimeToday(),
                settingsRepository.hourlyWage,
                gameRepository.getTotalGameTimeToday(),
                gameRepository.getGameSessionCountToday()
            ) { screenTime, wage, gameTime, gameSessions ->
                GeneralStats(
                    screenTime = screenTime ?: 0L,
                    wage = wage,
                    gameTime = gameTime ?: 0L,
                    gameSessions = gameSessions ?: 0
                )
            }

            combine(reelStatsFlow, generalStatsFlow) { reels, gen ->
                val distanceMeters = reels.totalReels * 0.15f
                val distanceKm = distanceMeters / 1000f

                val timeHours = gen.screenTime / (1000f * 60 * 60)
                val cost = timeHours * gen.wage

                AnalyticsUiState(
                    totalReels = reels.totalReels,
                    instagramReels = reels.igReels,
                    youtubeShorts = reels.ytShorts,
                    scrollDistanceKm = distanceKm,
                    screenTimeMs = gen.screenTime,
                    hourlyWage = gen.wage,
                    wastedTimeCost = cost,
                    gameTimeMs = gen.gameTime,
                    gameSessionsCount = gen.gameSessions
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}
