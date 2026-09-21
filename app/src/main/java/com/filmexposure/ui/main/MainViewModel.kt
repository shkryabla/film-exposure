package com.filmexposure.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmexposure.domain.model.AppSettings
import com.filmexposure.domain.model.DofResult
import com.filmexposure.domain.model.ExposurePair
import com.filmexposure.domain.model.ExposurePosting
import com.filmexposure.domain.model.ExposurePostingResult
import com.filmexposure.domain.model.MeterPoint
import com.filmexposure.domain.model.PointCategory
import com.filmexposure.domain.model.ScaleMode
import com.filmexposure.domain.repository.FilmRepository
import com.filmexposure.domain.repository.FormatRepository
import com.filmexposure.domain.repository.RigRepository
import com.filmexposure.domain.repository.SettingsRepository
import com.filmexposure.domain.usecase.CalculateBvUseCase
import com.filmexposure.domain.usecase.CalculateDofUseCase
import com.filmexposure.domain.usecase.CalculateExposurePostingUseCase
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
import kotlin.math.hypot

private const val MARKER_HIT_RADIUS_PX = 60f

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val rigRepository: RigRepository,
    private val formatRepository: FormatRepository,
    private val filmRepository: FilmRepository,
    private val calculateBv: CalculateBvUseCase,
    private val calculatePosting: CalculateExposurePostingUseCase,
    private val calculatePairs: CalculateValidPairsUseCase,
    private val calculateDof: CalculateDofUseCase,
    private val parseList: ParseTechnicalListUseCase,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    /** Реальный риг+плёнка из меню (§6.2/§6.3), если выбраны; иначе MockRig-заглушка. */
    val rigContext: StateFlow<RigContext> = settings
        .map { it.selectedRigId to it.selectedFilmId }
        .distinctUntilChanged()
        .map { (rigId, filmId) -> loadRigContext(rigId, filmId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RigContext.mock())

    private val _meterPoints = MutableStateFlow<List<MeterPoint>>(emptyList())
    val meterPoints: StateFlow<List<MeterPoint>> = _meterPoints

    private val _posting = MutableStateFlow(ExposurePosting.BALANCE)
    val posting: StateFlow<ExposurePosting> = _posting

    private val _evShift = MutableStateFlow(0f)
    val evShift: StateFlow<Float> = _evShift

    private val _selectedPairIndex = MutableStateFlow(0)
    val selectedPairIndex: StateFlow<Int> = _selectedPairIndex

    private val _focusDistanceM = MutableStateFlow(3f)
    val focusDistanceM: StateFlow<Float> = _focusDistanceM

    val postingResult: StateFlow<ExposurePostingResult?> = combine(_meterPoints, _posting) { points, posting ->
        if (points.isEmpty()) null else calculatePosting(points, posting)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val validPairs: StateFlow<List<ExposurePair>> = combine(postingResult, _evShift, rigContext) { result, shift, ctx ->
        val target = result?.evShooting ?: return@combine emptyList()
        calculatePairs(target + shift, ctx.apertures, ctx.shutters)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dof: StateFlow<DofResult?> = combine(validPairs, _selectedPairIndex, _focusDistanceM, rigContext) { pairs, index, distanceM, ctx ->
        val pair = pairs.getOrNull(index) ?: return@combine null
        val aperture = parseList.apertureValue(pair.aperture) ?: return@combine null
        calculateDof(ctx.focalMm, aperture, ctx.cocMm, distanceM * 1000f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private suspend fun loadRigContext(rigId: Long?, filmId: String?): RigContext {
        val rig = rigId?.let { rigRepository.getById(it) } ?: return RigContext.mock()
        val format = formatRepository.getAll().firstOrNull { it.id == rig.formatId }
        val film = filmId?.let { id -> filmRepository.getAll().firstOrNull { it.id == id } }
        val mock = RigContext.mock()
        return RigContext(
            apertures = rig.activeApertures,
            shutters = rig.activeSpeeds,
            focalMm = rig.focal.toFloat(),
            cocMm = format?.coc ?: mock.cocMm,
            filmIso = film?.iso ?: mock.filmIso,
            filmLatitudeMinus = film?.latitudeMinus ?: mock.filmLatitudeMinus,
            filmLatitudePlus = film?.latitudePlus ?: mock.filmLatitudePlus,
            isMock = false,
        )
    }

    /** Ev точки замера по текущему кадру камеры + калибровке + ISO текущей (или мок-)плёнки. */
    fun meterPointEv(frame: LuminanceFrame): Float {
        val bv = calculateBv.bv(
            signal = frame.averageLuminance,
            t0 = frame.exposureTimeSeconds,
            n0 = frame.aperture,
            iso0 = frame.isoSensitivity,
            calibrationConstant = settings.value.calibrationConstant,
        )
        return calculateBv.evAtFilmIso(bv, rigContext.value.filmIso)
    }

    /**
     * Тап по превью (§8.1). Тап рядом с существующим маркером — удаляет его (§7.7).
     * 4-й тап при уже 3 точках — сброс и новый цикл (§8.1).
     */
    fun onTap(x: Float, y: Float, ev: Float) {
        val current = _meterPoints.value
        val hit = current.firstOrNull { hypot((it.x - x).toDouble(), (it.y - y).toDouble()) < MARKER_HIT_RADIUS_PX }

        _meterPoints.value = when {
            hit != null -> current - hit
            current.size >= 3 -> listOf(MeterPoint(x, y, ev, PointCategory.MID))
            else -> current + MeterPoint(x, y, ev, PointCategory.MID)
        }
        _selectedPairIndex.value = 0
    }

    fun clearPoints() {
        _meterPoints.value = emptyList()
        _selectedPairIndex.value = 0
    }

    fun setPosting(mode: ExposurePosting) {
        _posting.value = mode
    }

    fun setEvShift(shift: Float) {
        _evShift.value = shift.coerceIn(-3f, 3f)
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

    fun toggleScaleMode() {
        viewModelScope.launch {
            settingsRepository.update {
                it.copy(scaleMode = if (it.scaleMode == ScaleMode.SIMPLE) ScaleMode.PRO else ScaleMode.SIMPLE)
            }
        }
    }
}
