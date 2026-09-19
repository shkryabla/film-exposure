package com.filmexposure.domain.usecase

import com.filmexposure.domain.model.ExposurePosting
import com.filmexposure.domain.model.ExposurePostingResult
import com.filmexposure.domain.model.MeterPoint
import com.filmexposure.domain.model.PointCategory
import javax.inject.Inject
import kotlin.math.max

/**
 * Категоризация точек замера и расчёт итогового Ev съёмки (ТЗ §4.5, §5.5).
 * На вход — уже созданные точки замера (x/y — координаты тапа на превью, ev — замеренное
 * значение); категория входных точек не используется и пересчитывается заново, x/y сохраняются
 * для последующей отрисовки маркеров (§7.7).
 *
 * Правила категоризации (согласовано по проекту):
 * - 1 точка  → всегда "Средний тон"; посадка недоступна, Ev_съёмки = Ev этой точки.
 * - 2 точки  → меньшая = "Тень", большая = "Свет"; "Средний тон" не создаётся; посадка доступна.
 * - 3 точки  → минимум = "Тень", максимум = "Свет", оставшаяся = "Средний тон".
 */
class CalculateExposurePostingUseCase @Inject constructor() {

    operator fun invoke(rawPoints: List<MeterPoint>, posting: ExposurePosting): ExposurePostingResult {
        require(rawPoints.isNotEmpty() && rawPoints.size <= 3) {
            "Ожидается от 1 до 3 замеров, получено ${rawPoints.size}"
        }

        val points = categorize(rawPoints)

        if (points.size == 1) {
            return ExposurePostingResult(
                points = points,
                evShooting = points.first().ev,
                postingAvailable = false,
                shadowLossStops = 0f,
                lightLossStops = 0f,
            )
        }

        val shadowEv = points.first { it.category == PointCategory.SHADOW }.ev
        val lightEv = points.first { it.category == PointCategory.LIGHT }.ev

        val evShooting = when (posting) {
            ExposurePosting.SHADOWS -> shadowEv + 2f
            ExposurePosting.HIGHLIGHTS -> lightEv - 2f
            ExposurePosting.BALANCE -> (shadowEv + lightEv) / 2f
        }

        val shadowLoss = max(0f, (shadowEv + 2f) - evShooting)
        val lightLoss = max(0f, evShooting - (lightEv - 2f))

        return ExposurePostingResult(
            points = points,
            evShooting = evShooting,
            postingAvailable = true,
            shadowLossStops = shadowLoss,
            lightLossStops = lightLoss,
        )
    }

    private fun categorize(raw: List<MeterPoint>): List<MeterPoint> {
        if (raw.size == 1) {
            return listOf(raw[0].copy(category = PointCategory.MID))
        }

        if (raw.size == 2) {
            val (shadow, light) = if (raw[0].ev <= raw[1].ev) raw[0] to raw[1] else raw[1] to raw[0]
            return listOf(
                shadow.copy(category = PointCategory.SHADOW),
                light.copy(category = PointCategory.LIGHT),
            )
        }

        val sorted = raw.sortedBy { it.ev }
        return listOf(
            sorted[0].copy(category = PointCategory.SHADOW),
            sorted[1].copy(category = PointCategory.MID),
            sorted[2].copy(category = PointCategory.LIGHT),
        )
    }
}
