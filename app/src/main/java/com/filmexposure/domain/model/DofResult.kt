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

/** Валидная пара диафрагма/выдержка под текущий Ev, с посчитанной ГРИП для выбранной дистанции. */
data class ExposurePair(
    val aperture: String,
    val shutter: String,
    val av: Float,
    val tv: Float,
    val dof: DofResult?,
)
