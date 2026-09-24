package com.filmexposure.domain.usecase

import com.filmexposure.domain.model.ApexValues
import com.filmexposure.domain.model.ExposurePair
import javax.inject.Inject
import kotlin.math.abs

/**
 * Все пары диафрагма/выдержка из АКТИВНОГО набора рига, дающие targetEv (§7.4: "только пары,
 * дающие нужный Ev"). Разные пары могут давать один и тот же Ev за счёт взаимозаместимости
 * диафрагмы и выдержки (f/8+1/125 ≡ f/11+1/60) — именно поэтому результат может быть списком,
 * а не одной парой; синхронные ленты (§7.4, §8.3) листают именно этот список.
 *
 * tolerance — допуск в EV-ступенях; риги со "step": "full" целых пар может не найтись ровно
 * (полные ступени реже точно попадают в targetEv, который обычно приходит с дробным Ev с замера) —
 * 1/6 стопа (~0.083) достаточно мягкий допуск, чтобы не оставлять ленты пустыми на full-step ригах.
 *
 * Bulb ("B") исключён — она не имеет определённой длительности для расчёта Ev.
 */
class CalculateValidPairsUseCase @Inject constructor(
    private val parseList: ParseTechnicalListUseCase,
) {

    operator fun invoke(
        targetEv: Float,
        apertureLabels: List<String>,
        shutterLabels: List<String>,
        toleranceEv: Float = 1f / 6f,
    ): List<ExposurePair> {
        val apertures = apertureLabels.mapNotNull { label ->
            parseList.apertureValue(label)?.let { label to it }
        }
        val shutters = shutterLabels.mapNotNull { label ->
            parseList.shutterSeconds(label)?.let { label to it }
        }

        val pairs = mutableListOf<ExposurePair>()
        for ((apertureLabel, n) in apertures) {
            val av = ApexValues.av(n)
            for ((shutterLabel, t) in shutters) {
                val tv = ApexValues.tv(t)
                if (abs((av + tv) - targetEv) <= toleranceEv) {
                    pairs.add(ExposurePair(aperture = apertureLabel, shutter = shutterLabel, av = av, tv = tv, dof = null))
                }
            }
        }
        return pairs.sortedBy { it.av }
    }
}
