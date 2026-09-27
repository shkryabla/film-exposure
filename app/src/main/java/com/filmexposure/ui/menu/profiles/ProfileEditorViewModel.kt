package com.filmexposure.ui.menu.profiles
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.ApertureSeries
import com.filmexposure.domain.model.FilmFormat
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.model.ShutterSeries
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
/** ISO убран (решение по проекту) — выбирается на главном экране, не в профиле. */
data class ProfileEditorState(
    val profileId: Long = 0,
    val name: String = "",
    val apertureSeries: ApertureSeries = ApertureSeries.MODERN,
    val shutterSeries: ShutterSeries = ShutterSeries.MODERN,
    val focalInput: String = "",
    val formatId: String? = null,
    val formats: List<FilmFormat> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
) {
    val isValid: Boolean
        get() = name.isNotBlank() && focalInput.toIntOrNull() != null && formatId != null
}
@HiltViewModel
class ProfileEditorViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val formatRepository: FormatRepository,
    private val settingsRepository: SettingsRepository,
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
                        apertureSeries = existing.apertureSeries,
                        shutterSeries = existing.shutterSeries,
                        focalInput = existing.focalMm.toString(),
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
    fun setApertureSeries(v: ApertureSeries) = _state.update { it.copy(apertureSeries = v) }
    fun setShutterSeries(v: ShutterSeries) = _state.update { it.copy(shutterSeries = v) }
    fun setFocal(v: String) = _state.update { it.copy(focalInput = v.filter { c -> c.isDigit() }) }
    fun setFormat(v: FilmFormat) = _state.update { it.copy(formatId = v.id) }
    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            val savedId = profileRepository.upsert(
                Profile(
                    id = s.profileId,
                    name = s.name,
                    apertureSeries = s.apertureSeries,
                    shutterSeries = s.shutterSeries,
                    focalMm = s.focalInput.toInt(),
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
