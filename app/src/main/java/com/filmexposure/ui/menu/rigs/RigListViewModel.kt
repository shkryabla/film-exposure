package com.filmexposure.ui.menu.rigs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.model.Lens
import com.filmexposure.domain.model.Rig
import com.filmexposure.domain.repository.CameraRepository
import com.filmexposure.domain.repository.LensRepository
import com.filmexposure.domain.repository.RigRepository
import com.filmexposure.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RigListViewModel @Inject constructor(
    private val rigRepository: RigRepository,
    private val cameraRepository: CameraRepository,
    private val lensRepository: LensRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val rigs: StateFlow<List<Rig>> = rigRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedRigId: StateFlow<Long?> = settingsRepository.settings
        .map { it.selectedRigId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _cameras = MutableStateFlow<List<Camera>>(emptyList())
    val cameras: StateFlow<List<Camera>> = _cameras

    private val _lenses = MutableStateFlow<List<Lens>>(emptyList())
    val lenses: StateFlow<List<Lens>> = _lenses

    init {
        viewModelScope.launch { _cameras.value = cameraRepository.getAll() }
        viewModelScope.launch { _lenses.value = lensRepository.getAll() }
    }

    fun deleteRig(id: Long) {
        viewModelScope.launch { rigRepository.delete(id) }
    }

    /** Выбрать риг для съёмки на главном экране (используется там через MainViewModel.rigContext). */
    fun selectRig(id: Long) {
        viewModelScope.launch { settingsRepository.update { it.copy(selectedRigId = id) } }
    }
}
