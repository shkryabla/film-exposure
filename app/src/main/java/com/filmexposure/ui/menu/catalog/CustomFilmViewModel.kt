package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Film
import com.filmexposure.domain.model.ReciprocityTable
import com.filmexposure.domain.repository.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val FILM_TYPES = listOf("bw_negative", "color_negative", "slide")

data class CustomFilmState(
    val name: String = "",
    val isoInput: String = "",
    val type: String = FILM_TYPES.first(),
    val latitudeMinusInput: String = "2",
    val latitudePlusInput: String = "2",
    val pushMaxInput: String = "0",
    val notes: String = "",
    val isSaved: Boolean = false,
) {
    val isValid: Boolean get() = name.isNotBlank() && isoInput.toIntOrNull() != null
}

/**
 * УПРОЩЕНИЕ v1: таблица взаимозаместимости (Шварцшильд, §4.3) для custom-плёнок не заполняется
 * через UI — сохраняется пустой (threshold=1с, без коррекции). Ввод полной таблицы {metered,actual}
 * через форму — отдельная, более объёмная задача UI, в ТЗ не специфицирована подробно.
 */
@HiltViewModel
class CustomFilmViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomFilmState())
    val state: StateFlow<CustomFilmState> = _state.asStateFlow()

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setIso(v: String) = _state.update { it.copy(isoInput = v.filter { c -> c.isDigit() }) }
    fun setType(v: String) = _state.update { it.copy(type = v) }
    fun setLatitudeMinus(v: String) = _state.update { it.copy(latitudeMinusInput = v.filter { c -> c.isDigit() }) }
    fun setLatitudePlus(v: String) = _state.update { it.copy(latitudePlusInput = v.filter { c -> c.isDigit() }) }
    fun setPushMax(v: String) = _state.update { it.copy(pushMaxInput = v.filter { c -> c.isDigit() }) }
    fun setNotes(v: String) = _state.update { it.copy(notes = v) }

    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            filmRepository.addCustom(
                Film(
                    id = "",
                    name = s.name,
                    brand = null,
                    iso = s.isoInput.toInt(),
                    type = s.type,
                    latitudeMinus = s.latitudeMinusInput.toIntOrNull() ?: 2,
                    latitudePlus = s.latitudePlusInput.toIntOrNull() ?: 2,
                    pushMax = s.pushMaxInput.toIntOrNull() ?: 0,
                    reciprocity = ReciprocityTable(threshold = 1f, points = emptyList()),
                    isCustom = true,
                    notes = s.notes,
                ),
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
