package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Lens
import com.filmexposure.domain.repository.LensRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LensListViewModel @Inject constructor(
    private val repository: LensRepository,
) : ViewModel() {
    private val _lenses = MutableStateFlow<List<Lens>>(emptyList())
    val lenses: StateFlow<List<Lens>> = _lenses

    init {
        viewModelScope.launch { _lenses.value = repository.getAll() }
    }

    fun refresh() {
        viewModelScope.launch { _lenses.value = repository.getAll() }
    }
}
