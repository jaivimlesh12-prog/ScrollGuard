package com.scrollguard.ui.screen.applimits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrollguard.data.db.entity.AppLimitEntity
import com.scrollguard.data.repository.AppLimitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLimitsViewModel @Inject constructor(
    private val appLimitRepository: AppLimitRepository
) : ViewModel() {

    val appLimits: StateFlow<List<AppLimitEntity>> = appLimitRepository.getAllLimits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateLimit(
        packageName: String,
        appName: String,
        limitMinutes: Int,
        isStrict: Boolean
    ) {
        viewModelScope.launch {
            val entity = AppLimitEntity(
                packageName = packageName,
                appName = appName,
                dailyLimitMs = limitMinutes * 60 * 1000L,
                isStrict = isStrict,
                isEnabled = true
            )
            appLimitRepository.setLimit(entity)
        }
    }

    fun deleteLimit(id: Long) {
        viewModelScope.launch {
            appLimitRepository.removeLimit(id)
        }
    }
}
