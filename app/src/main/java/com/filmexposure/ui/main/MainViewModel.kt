package com.filmexposure.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.model.DofResult
import com.filmexposure.domain.model.ExposurePair
import com.filmexposure.domain.model.Profile
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.ProfileRepository
import com.filmexposure.domain.repository.SettingsRepository
import com.filmexposure.domain.usecase.CalculateBvUseCase
import com.filmexposure.domain.usecase.CalculateDofUseCase
import com.filmexposure.domain.usecase.CalculateValidPairsUseCase
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import com.filmexposure.ui.camera.LuminanceFrame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Не чаще раза в ~200мс пересчитываем Ev — иначе живой замер дёргается от шума сенсора кадр в кадр. */
private const val LIVE_UPDATE_THROTTLE_MS = 200L

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository,
    private val formatRepository: FormatRepository,
    private val calculateBv: CalculateBvUseCase,
    private val calculatePairs: CalculateValidPairsUseCase,
    private val calculateDof: CalculateDofUseCase,
    private val parseList: ParseTechnicalListUseCase,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    /** Активный профиль (§6.2 "Профили"); null — профиль не выбран, замер невозможен. */
    val activeProfile: StateFlow<Profile?> = settings
        .map { it.selectedProfileId }
        .distinctUntilChanged()
        .map { id -> id?.let { profileRepository.getById(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isFrozen = MutableStateFlow(false)
    val isFrozen: StateFlow<Boolean> = _isFrozen

    private val _liveEv = MutableStateFlow<Float?>(null)
    private val _frozenEv = MutableStateFlow<Float?>(null)

    /** Текущий Ev: живой, пока не зафиксировано; после "Зафиксировать" — заморожен вместе с кадром. */
    val currentEv: StateFlow<Float?> = combine(_isFrozen, _liveEv, _frozenEv) { frozen, live, frozenVal ->
        if (frozen) frozenVal else live
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _selectedPairIndex = MutableStateFlow(0)
    val selectedPairIndex: StateFlow<Int> = _selectedPairIndex

    private val _focusDistanceM = MutableStateFlow(3f)
    val focusDistanceM: StateFlow<Float> = _focusDistanceM

    val validPairs: StateFlow<List<ExposurePair>> = combine(currentEv, activeProfile) { ev, profile ->
        if (ev == null || profile == null) emptyList() else calculatePairs(ev, profile.apertures, profile.shutters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dof: StateFlow<DofResult?> = combine(validPairs, _selectedPairIndex, _focusDistanceM, activeProfile) { pairs, index, distanceM, profile ->
        val pair = pairs.getOrNull(index) ?: return@combine null
        val p = profile ?: return@combine null
        val aperture = parseList.apertureValue(pair.aperture) ?: return@combine null
        val coc = formatRepository.getAll().firstOrNull { it.id == p.formatId }?.coc ?: return@combine null
        calculateDof(p.focalMm.toFloat(), aperture, coc, distanceM * 1000f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private var lastFrameProcessedAtMs = 0L

    init {
        // Смена Ev (сцена изменилась) — сбрасываем выбор пары на первую, иначе индекс может
        // указывать на пару из уже неактуального набора.
        viewModelScope.launch {
            currentEv.distinctUntilChanged().collect { _selectedPairIndex.value = 0 }
        }
    }

    /** Вызывается из UI при каждом новом кадре камеры (§8.1 — теперь непрерывно, без тапов). */
    fun onLiveFrame(frame: LuminanceFrame) {
        if (_isFrozen.value) return
        val now = System.currentTimeMillis()
        if (now - lastFrameProcessedAtMs < LIVE_UPDATE_THROTTLE_MS) return
        lastFrameProcessedAtMs = now

        val profile = activeProfile.value ?: return
        val bv = calculateBv.bv(
            signal = frame.averageLuminance,
            t0 = frame.exposureTimeSeconds,
            n0 = frame.aperture,
            iso0 = frame.isoSensitivity,
            calibrationConstant = settings.value.calibrationConstant,
        )
        _liveEv.value = calculateBv.evAtFilmIso(bv, profile.iso)
    }

    /**
     * "Зафиксировать" (бывшая пауза, §7.6): стопорит и кадр, и Ev, и пары одновременно.
     * Возвращает новое состояние — UI по нему решает, звать ли CameraController.freeze()/unfreeze()
     * (сама заморозка Bitmap — забота UI-слоя, ViewModel камеру не держит).
     */
    fun toggleFreeze(): Boolean {
        val next = !_isFrozen.value
        if (next) _frozenEv.value = _liveEv.value
        _isFrozen.value = next
        return next
    }

    fun selectPairIndex(index: Int) {
        _selectedPairIndex.value = index
    }

    fun setFocusDistanceM(distanceM: Float) {
        _focusDistanceM.value = distanceM.coerceIn(0.3f, 100f)
    }

    fun toggleBW(value: Boolean) {
        viewModelScope.launch { settingsRepository.update { it.copy(isBW = value) } }
    }
}
