package com.filmexposure.domain.usecase

import com.filmexposure.domain.model.ReciprocityTable
import javax.inject.Inject

/**
 * Перевод замеренной выдержки в реальную с учётом взаимозаместимости плёнки (ТЗ §4.3).
 * Расчёт — через таблицу {metered, actual} с линейной интерполяцией (а не степенной
 * формулой t_real = t_metered^(1/p)), так как справочник плёнок хранит именно таблицы.
 */
class CalculateReciprocityUseCase @Inject constructor() {

    operator fun invoke(meteredSeconds: Float, table: ReciprocityTable): Float {
        if (meteredSeconds <= table.threshold || table.points.isEmpty()) return meteredSeconds

        val points = table.points.sortedBy { it.metered }

        // Ниже первой точки таблицы, но выше порога — линейная экстраполяция по первым двум точкам.
        if (meteredSeconds <= points.first().metered) {
            if (points.size < 2) return points.first().actual
            return interpolate(meteredSeconds, points[0], points[1])
        }

        // Выше последней точки — экстраполяция по последним двум точкам.
        if (meteredSeconds >= points.last().metered) {
            if (points.size < 2) return points.last().actual
            return interpolate(meteredSeconds, points[points.size - 2], points[points.size - 1])
        }

        // Внутри таблицы — интерполяция между соседними точками, охватывающими значение.
        for (i in 0 until points.size - 1) {
            val a = points[i]
            val b = points[i + 1]
            if (meteredSeconds in a.metered..b.metered) {
                return interpolate(meteredSeconds, a, b)
            }
        }

        return meteredSeconds
    }

    private fun interpolate(
        x: Float,
        a: com.filmexposure.domain.model.ReciprocityPoint,
        b: com.filmexposure.domain.model.ReciprocityPoint,
    ): Float {
        val ratio = (x - a.metered) / (b.metered - a.metered)
        return a.actual + ratio * (b.actual - a.actual)
    }
}
