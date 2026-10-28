package com.scrollguard.ui.screen.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.db.entity.ChallengeEntity
import com.scrollguard.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    val challenges: StateFlow<List<ChallengeEntity>> = challengeRepository.getAllChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createSampleChallenge(title: String, description: String, targetVal: Long) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            challengeRepository.createChallenge(
                title = title,
                description = description,
                type = "daily",
                targetType = "reels",
                targetValue = targetVal,
                startDate = now,
                endDate = now + 86400000L
            )
        }
    }
}
