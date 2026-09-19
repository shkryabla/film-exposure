package com.filmexposure.domain.model

/**
 * Настройки приложения (DataStore, ТЗ §5.3).
 * calibrationConstant — константа C из §4.2, результат экрана калибровки при первом запуске;
 * null означает, что калибровка ещё не пройдена (экран калибровки блокирует основной экран).
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
    val calibrationConstant: Float? = null,
)
