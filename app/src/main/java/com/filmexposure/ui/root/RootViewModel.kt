package com.filmexposure.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** null = настройки ещё не загружены (первый кадр после старта процесса). */
@HiltViewModel
class RootViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val isCalibrated: StateFlow<Boolean?> = settingsRepository.settings
        .map { it.calibrationConstant != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
