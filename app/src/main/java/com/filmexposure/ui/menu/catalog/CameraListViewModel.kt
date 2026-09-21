package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val repository: CameraRepository,
) : ViewModel() {
    private val _cameras = MutableStateFlow<List<Camera>>(emptyList())
    val cameras: StateFlow<List<Camera>> = _cameras

    init {
        viewModelScope.launch { _cameras.value = repository.getAll() }
    }

    fun refresh() {
        viewModelScope.launch { _cameras.value = repository.getAll() }
    }
}
