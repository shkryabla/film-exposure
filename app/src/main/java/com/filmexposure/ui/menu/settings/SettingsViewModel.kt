package com.filmexposure.ui.menu.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.model.ButtonSide
import com.filmexposure.domain.model.DistanceUnit
import com.filmexposure.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setDistanceUnit(unit: DistanceUnit) = update { it.copy(distanceUnit = unit) }
    fun setPauseButtonSide(side: ButtonSide) = update { it.copy(pauseButtonSide = side) }
    fun setBW(value: Boolean) = update { it.copy(isBW = value) }

    private fun update(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { settingsRepository.update(transform) }
    }
}
