package com.filmexposure.ui.menu.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.model.StopStep
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileEditorState(
    val profileId: Long = 0,
    val name: String = "",
    val aperturesInput: String = "",
    val aperturesStep: StopStep = StopStep.FULL,
    val shuttersInput: String = "",
    val shuttersStep: StopStep = StopStep.FULL,
    val focalInput: String = "",
    val isoInput: String = "",
    val formatId: String? = null,
    val formats: List<FilmFormat> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
) {
    val isValid: Boolean
        get() = name.isNotBlank() && aperturesInput.isNotBlank() && shuttersInput.isNotBlank() &&
            focalInput.toIntOrNull() != null && isoInput.toIntOrNull() != null && formatId != null
}

@HiltViewModel
class ProfileEditorViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val formatRepository: FormatRepository,
    private val settingsRepository: SettingsRepository,
    private val parseList: ParseTechnicalListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditorState())
    val state: StateFlow<ProfileEditorState> = _state.asStateFlow()

    fun load(profileId: Long?) {
        viewModelScope.launch {
            val formats = formatRepository.getAll()
            val existing = profileId?.let { profileRepository.getById(it) }
            _state.update {
                if (existing != null) {
                    it.copy(
                        profileId = existing.id,
                        name = existing.name,
                        aperturesInput = existing.apertures.joinToString(";"),
                        aperturesStep = existing.aperturesStep,
                        shuttersInput = existing.shutters.joinToString(";"),
                        shuttersStep = existing.shuttersStep,
                        focalInput = existing.focalMm.toString(),
                        isoInput = existing.iso.toString(),
                        formatId = existing.formatId,
                        formats = formats,
                        isLoading = false,
                    )
                } else {
                    it.copy(formats = formats, formatId = formats.firstOrNull()?.id, isLoading = false)
                }
            }
        }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setAperturesInput(v: String) = _state.update { it.copy(aperturesInput = v) }
    fun setAperturesStep(v: StopStep) = _state.update { it.copy(aperturesStep = v) }
    fun setShuttersInput(v: String) = _state.update { it.copy(shuttersInput = v) }
    fun setShuttersStep(v: StopStep) = _state.update { it.copy(shuttersStep = v) }
    fun setFocal(v: String) = _state.update { it.copy(focalInput = v.filter { c -> c.isDigit() }) }
    fun setIso(v: String) = _state.update { it.copy(isoInput = v.filter { c -> c.isDigit() }) }
    fun setFormat(v: FilmFormat) = _state.update { it.copy(formatId = v.id) }

    fun parsedAperturesPreview(): List<String> = parseList.parseApertures(_state.value.aperturesInput)
    fun parsedShuttersPreview(): List<String> = parseList.parseSpeeds(_state.value.shuttersInput)

    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            val savedId = profileRepository.upsert(
                Profile(
                    id = s.profileId,
                    name = s.name,
                    apertures = parsedAperturesPreview(),
                    aperturesStep = s.aperturesStep,
                    shutters = parsedShuttersPreview(),
                    shuttersStep = s.shuttersStep,
                    focalMm = s.focalInput.toInt(),
                    iso = s.isoInput.toInt(),
                    formatId = s.formatId!!,
                ),
            )
            // Сохранённый профиль сразу используется на главном экране — та же логика, что раньше
            // была у рига: иначе выбор параметров здесь никак не применяется, пока юзер отдельно
            // не выберет профиль в списке.
            settingsRepository.update { it.copy(selectedProfileId = savedId) }
            _state.update { it.copy(isSaved = true) }
        }
    }
}
