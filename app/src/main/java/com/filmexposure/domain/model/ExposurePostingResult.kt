package com.filmexposure.domain.model

/**
 * Результат посадки экспозиции (ТЗ §5.5).
 * postingAvailable = false при одной точке замера — нет диапазона тень/свет,
 * поэтому режимы SHADOWS/HIGHLIGHTS/BALANCE неприменимы, evShooting = единственный замер.
 */
data class ExposurePostingResult(
    val points: List<MeterPoint>,
    val evShooting: Float,
    val postingAvailable: Boolean,
    val shadowLossStops: Float,
    val lightLossStops: Float,
)
