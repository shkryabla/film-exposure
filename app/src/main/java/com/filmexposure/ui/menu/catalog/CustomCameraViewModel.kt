package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.repository.CameraRepository
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Тип затвора, X-sync и шаг диафрагмы/выдержки сейчас нигде не участвуют в расчётах и не
 * показываются в справочнике — не просим пользователя их вводить, дефолтим тихо (наиболее
 * частый случай для фотоаппарата с фокальным затвором и полными ступенями).
 */
private const val DEFAULT_SHUTTER_TYPE = "focal_plane"
private const val DEFAULT_STEP = "full"

data class CustomCameraState(
    val name: String = "",
    val formatId: String? = null,
    val speedsInput: String = "",
    val notes: String = "",
    val formats: List<FilmFormat> = emptyList(),
    val isSaved: Boolean = false,
) {
    val isValid: Boolean get() = name.isNotBlank() && formatId != null && speedsInput.isNotBlank()
}

@HiltViewModel
class CustomCameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val formatRepository: FormatRepository,
    private val parseList: ParseTechnicalListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomCameraState())
    val state: StateFlow<CustomCameraState> = _state.asStateFlow()

    init {
        viewModelScope.launch { _state.update { it.copy(formats = formatRepository.getAll()) } }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setFormat(v: FilmFormat) = _state.update { it.copy(formatId = v.id) }
    fun setSpeedsInput(v: String) = _state.update { it.copy(speedsInput = v) }
    fun setNotes(v: String) = _state.update { it.copy(notes = v) }

    fun parsedSpeedsPreview(): List<String> = parseList.parseSpeeds(_state.value.speedsInput)

    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            cameraRepository.addCustom(
                Camera(
                    id = "",
                    name = s.name,
                    brand = null,
                    formatId = s.formatId!!,
                    shutterType = DEFAULT_SHUTTER_TYPE,
                    speeds = parsedSpeedsPreview(),
                    xSync = null,
                    step = DEFAULT_STEP,
                    isCustom = true,
                    notes = s.notes,
                ),
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
