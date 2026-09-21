package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Film
import com.filmexposure.domain.repository.FilmRepository
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
class FilmListViewModel @Inject constructor(
    private val repository: FilmRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> = _films

    val selectedFilmId: StateFlow<String?> = settingsRepository.settings
        .map { it.selectedFilmId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { _films.value = repository.getAll() }
    }

    fun refresh() {
        viewModelScope.launch { _films.value = repository.getAll() }
    }

    /** Выбрать плёнку как заряженную в текущий риг (§6.1 использует её ISO/широту). */
    fun selectFilm(id: String) {
        viewModelScope.launch { settingsRepository.update { it.copy(selectedFilmId = id) } }
    }
}
