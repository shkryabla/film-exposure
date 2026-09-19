package com.filmexposure.ui.calibration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.repository.SettingsRepository
import com.filmexposure.domain.usecase.CalculateBvUseCase
import com.filmexposure.ui.camera.LuminanceFrame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val calculateBv: CalculateBvUseCase,
) : ViewModel() {

    /** Bv по формуле §4.2 без калибровки (C=0) — точка отсчёта для расчёта константы. */
    fun measureUncalibratedBv(frame: LuminanceFrame): Float = calculateBv.bv(
        signal = frame.averageLuminance,
        t0 = frame.exposureTimeSeconds,
        n0 = frame.aperture,
        iso0 = frame.isoSensitivity,
        calibrationConstant = 0f,
    )

    fun saveCalibration(constant: Float, onSaved: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.update { it.copy(calibrationConstant = constant) }
            onSaved()
        }
    }
}
