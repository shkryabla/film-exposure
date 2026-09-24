package com.filmexposure.ui.menu.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val profiles: StateFlow<List<Profile>> = profileRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedProfileId: StateFlow<Long?> = settingsRepository.settings
        .map { it.selectedProfileId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectProfile(id: Long) {
        viewModelScope.launch { settingsRepository.update { it.copy(selectedProfileId = id) } }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            profileRepository.delete(id)
            // Если удалили выбранный профиль — сбрасываем выбор, иначе главный экран будет
            // тихо ссылаться на несуществующий id.
            if (selectedProfileId.value == id) {
                settingsRepository.update { it.copy(selectedProfileId = null) }
            }
        }
    }
}
