package com.filmexposure.domain.model

import kotlin.math.log2

/**
 * Система APEX (ТЗ §4.1): Ev = Av + Tv = Bv + Sv.
 */
data class ApexValues(
    val av: Float,
    val tv: Float,
    val sv: Float,
    val bv: Float,
    val ev: Float,
) {
    companion object {
        /** N — диафрагменное число (f/N). */
        fun av(n: Float): Float = 2f * log2(n)

        /** t — выдержка в секундах. */
        fun tv(t: Float): Float = log2(1f / t)

        /** Экспопара для базового ISO 100. */
        fun sv(iso: Int): Float = log2(iso / 100f)
    }
}
