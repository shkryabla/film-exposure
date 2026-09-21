package com.filmexposure.ui.menu.rigs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Camera
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.model.Lens
import com.filmexposure.domain.model.Rig
import com.filmexposure.domain.repository.CameraRepository
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.LensRepository
import com.filmexposure.domain.repository.RigRepository
import com.filmexposure.domain.repository.SettingsRepository
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RigEditorState(
    val rigId: Long = 0,
    val name: String = "",
    val cameraId: String? = null,
    val lensId: String? = null,
    val formatId: String? = null,
    val focalInput: String = "",
    val activeSpeedsInput: String = "",
    val activeAperturesInput: String = "",
    val notes: String = "",
    val cameras: List<Camera> = emptyList(),
    val lenses: List<Lens> = emptyList(),
    val formats: List<FilmFormat> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
) {
    val isValid: Boolean
        get() = name.isNotBlank() && cameraId != null && lensId != null && formatId != null &&
            focalInput.toIntOrNull() != null
}

@HiltViewModel
class RigEditorViewModel @Inject constructor(
    private val rigRepository: RigRepository,
    private val cameraRepository: CameraRepository,
    private val lensRepository: LensRepository,
    private val formatRepository: FormatRepository,
    private val parseList: ParseTechnicalListUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RigEditorState())
    val state: StateFlow<RigEditorState> = _state.asStateFlow()

    /** rigId == null -> создание нового рига; иначе загружает существующий для редактирования. */
    fun load(rigId: Long?) {
        viewModelScope.launch {
            val cameras = cameraRepository.getAll()
            val lenses = lensRepository.getAll()
            val formats = formatRepository.getAll()
            val existing = rigId?.let { rigRepository.getById(it) }

            _state.update {
                if (existing != null) {
                    it.copy(
                        rigId = existing.id,
                        name = existing.name,
                        cameraId = existing.cameraId,
                        lensId = existing.lensId,
                        formatId = existing.formatId,
                        focalInput = existing.focal.toString(),
                        activeSpeedsInput = existing.activeSpeeds.joinToString(";"),
                        activeAperturesInput = existing.activeApertures.joinToString(";"),
                        notes = existing.notes,
                        cameras = cameras,
                        lenses = lenses,
                        formats = formats,
                        isLoading = false,
                    )
                } else {
                    it.copy(cameras = cameras, lenses = lenses, formats = formats, isLoading = false)
                }
            }
        }
    }

    fun setName(value: String) = _state.update { it.copy(name = value) }

    /** При выборе камеры — по умолчанию подставляем полный список её выдержек и формат (редактируемо). */
    fun setCamera(camera: Camera) = _state.update {
        it.copy(
            cameraId = camera.id,
            formatId = it.formatId ?: camera.formatId,
            activeSpeedsInput = if (it.activeSpeedsInput.isBlank()) camera.speeds.joinToString(";") else it.activeSpeedsInput,
        )
    }

    /** При выборе объектива — по умолчанию подставляем полный список его диафрагм (редактируемо). */
    fun setLens(lens: Lens) = _state.update {
        it.copy(
            lensId = lens.id,
            focalInput = it.focalInput.ifBlank { lens.focalMin.toString() },
            activeAperturesInput = if (it.activeAperturesInput.isBlank()) lens.apertures.joinToString(";") else it.activeAperturesInput,
        )
    }

    fun setFormat(format: FilmFormat) = _state.update { it.copy(formatId = format.id) }
    fun setFocal(value: String) = _state.update { it.copy(focalInput = value.filter { c -> c.isDigit() }) }
    fun setActiveSpeeds(value: String) = _state.update { it.copy(activeSpeedsInput = value) }
    fun setActiveApertures(value: String) = _state.update { it.copy(activeAperturesInput = value) }
    fun setNotes(value: String) = _state.update { it.copy(notes = value) }

    fun parsedSpeedsPreview(): List<String> = parseList.parseSpeeds(_state.value.activeSpeedsInput)
    fun parsedAperturesPreview(): List<String> = parseList.parseApertures(_state.value.activeAperturesInput)

    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            val savedId = rigRepository.upsert(
                Rig(
                    id = s.rigId,
                    name = s.name,
                    cameraId = s.cameraId!!,
                    lensId = s.lensId!!,
                    formatId = s.formatId!!,
                    activeSpeeds = parsedSpeedsPreview(),
                    activeApertures = parsedAperturesPreview(),
                    focal = s.focalInput.toInt(),
                    notes = s.notes,
                ),
            )
            // Сохранённый риг сразу используется на главном экране — иначе выбор камеры/объектива
            // здесь никак не применяется, пока пользователь отдельно не выберет риг в списке.
            settingsRepository.update { it.copy(selectedRigId = savedId) }
            _state.update { it.copy(isSaved = true) }
        }
    }
}
