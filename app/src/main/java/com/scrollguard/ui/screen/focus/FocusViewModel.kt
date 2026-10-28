package com.scrollguard.ui.screen.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isFocusModeActive: StateFlow<Boolean> = settingsRepository.isFocusModeActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleFocusMode() {
        viewModelScope.launch {
            val current = isFocusModeActive.value
            settingsRepository.setFocusModeActive(!current)
        }
    }
}
