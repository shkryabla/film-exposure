package com.filmexposure.domain.model

/**
 * Дефолтная калибровочная константа C (§4.2) для устройства, которое ещё не калибровали.
 *
 * ВАЖНО: это инженерная оценка, а не справочное/эмпирическое значение — единого "типичного C
 * для камерофонов" не существует (разброс между моделями большой и непубличный, см. обсуждение
 * калибровки). Оценка получена из допущения, что автоэкспозиция телефона на ISO100 целится в
 * стандартный серый таргет ~118/255 при sRGB-гамме (18% отражение):
 *   C_default = log2(100 / 118) ≈ -0.24
 * Согласована с собственным AE телефона, но не проверена эмпирически. Уточняется калибровкой.
 */
const val DEFAULT_CALIBRATION_CONSTANT = -0.24f

/**
 * Настройки приложения (DataStore, ТЗ §5.3).
 * calibrationConstant — константа C из §4.2. Калибровка НЕобязательна (решение по проекту):
 * без неё используется [DEFAULT_CALIBRATION_CONSTANT], а isCalibrated=false — сигнал для UI
 * показать ненавязчивую пометку "не откалибровано", не блокируя работу.
 */
data class AppSettings(
    val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    val meteringPoints: Int = 3,
    val scaleMode: ScaleMode = ScaleMode.SIMPLE,
    val showRuleOfThirds: Boolean = true,
    val showIntersections: Boolean = false,
    val showZoneOverlay: Boolean = false,
    val pauseButtonSide: ButtonSide = ButtonSide.RIGHT,
    val isBW: Boolean = false,
    val calibrationConstant: Float = DEFAULT_CALIBRATION_CONSTANT,
    val isCalibrated: Boolean = false,
    /** Риг, выбранный для съёмки на главном экране (§6.2 "Мои риги"); null — риг не выбран. */
    val selectedRigId: Long? = null,
    /** Плёнка, заряженная в выбранный риг (§6.2 "Плёнки"); null — плёнка не выбрана. */
    val selectedFilmId: String? = null,
)
