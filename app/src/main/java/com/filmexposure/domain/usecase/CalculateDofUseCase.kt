package com.filmexposure.domain.usecase

import com.filmexposure.domain.model.DofResult
import javax.inject.Inject

/**
 * Расчёт ГРИП и гиперфокального расстояния (ТЗ §4.4).
 * Все линейные величины — в одних и тех же единицах (мм или м), лишь бы f, s и c были согласованы.
 *
 * H = f²/(N·c) + f
 * s < H  → обычный расчёт Dn/Df
 * s >= H → Df = ∞ (в т.ч. предельный случай s → ∞, тогда Dn → H)
 */
class CalculateDofUseCase @Inject constructor() {

    operator fun invoke(focalLength: Float, aperture: Float, coc: Float, focusDistance: Float): DofResult {
        val hyperfocal = focalLength * focalLength / (aperture * coc) + focalLength

        val near: Float
        val far: Float

        if (focusDistance >= hyperfocal) {
            far = Float.POSITIVE_INFINITY
            near = hyperfocal * focusDistance / (hyperfocal + (focusDistance - focalLength))
        } else {
            near = hyperfocal * focusDistance / (hyperfocal + (focusDistance - focalLength))
            far = hyperfocal * focusDistance / (hyperfocal - (focusDistance - focalLength))
        }

        val total = if (far.isInfinite()) Float.POSITIVE_INFINITY else far - near
        return DofResult(near = near, far = far, hyperfocal = hyperfocal, total = total)
    }
}
