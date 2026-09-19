package com.filmexposure.domain.model

/**
 * Результат расчёта ГРИП (ТЗ §4.4).
 * far == Float.POSITIVE_INFINITY, если дальняя граница резкости уходит в бесконечность
 * (дистанция фокусировки >= гиперфокальному расстоянию).
 */
data class DofResult(
    val near: Float,
    val far: Float,
    val hyperfocal: Float,
    val total: Float,
)

/**
 * Валидная пара диафрагма/выдержка под текущий риг, с посчитанной ГРИП для выбранной дистанции.
 */
data class ExposurePair(
    val aperture: String,
    val shutter: String,
    val av: Float,
    val tv: Float,
    val dof: DofResult?,
)

/** Точка замера экспозиции на превью камеры (ТЗ §4.5, §8.1). */
data class MeterPoint(
    val x: Float,
    val y: Float,
    val ev: Float,
    val category: PointCategory,
)
