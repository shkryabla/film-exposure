package com.filmexposure.ui.menu.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.Lens
import com.filmexposure.domain.repository.LensRepository
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomLensState(
    val name: String = "",
    val focalMinInput: String = "",
    val focalMaxInput: String = "",
    val aperturesInput: String = "",
    val minFocusInput: String = "",
    val mount: String = "",
    val step: String = TECH_STEPS.first(),
    val notes: String = "",
    val isSaved: Boolean = false,
) {
    val isValid: Boolean
        get() = name.isNotBlank() && focalMinInput.toIntOrNull() != null &&
            focalMaxInput.toIntOrNull() != null && aperturesInput.isNotBlank() &&
            minFocusInput.toFloatOrNull() != null
}

@HiltViewModel
class CustomLensViewModel @Inject constructor(
    private val lensRepository: LensRepository,
    private val parseList: ParseTechnicalListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomLensState())
    val state: StateFlow<CustomLensState> = _state.asStateFlow()

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setFocalMin(v: String) = _state.update { it.copy(focalMinInput = v.filter { c -> c.isDigit() }) }
    fun setFocalMax(v: String) = _state.update { it.copy(focalMaxInput = v.filter { c -> c.isDigit() }) }
    fun setAperturesInput(v: String) = _state.update { it.copy(aperturesInput = v) }
    fun setMinFocus(v: String) = _state.update { it.copy(minFocusInput = v.filter { c -> c.isDigit() || c == '.' }) }
    fun setMount(v: String) = _state.update { it.copy(mount = v) }
    fun setStep(v: String) = _state.update { it.copy(step = v) }
    fun setNotes(v: String) = _state.update { it.copy(notes = v) }

    fun parsedAperturesPreview(): List<String> = parseList.parseApertures(_state.value.aperturesInput)

    fun save() {
        val s = _state.value
        if (!s.isValid) return
        viewModelScope.launch {
            lensRepository.addCustom(
                Lens(
                    id = "",
                    name = s.name,
                    brand = null,
                    focalMin = s.focalMinInput.toInt(),
                    focalMax = s.focalMaxInput.toInt(),
                    apertures = parsedAperturesPreview(),
                    minFocus = s.minFocusInput.toFloat(),
                    mount = s.mount.ifBlank { null },
                    step = s.step,
                    isCustom = true,
                    notes = s.notes,
                ),
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
