package com.filmexposure.domain

import com.filmexposure.domain.usecase.CalculateBvUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateBvUseCaseTest {
    private val useCase = CalculateBvUseCase()

    @Test
    fun `calibration constant shifts Bv additively`() {
        val withoutC = useCase.bv(signal = 100f, t0 = 1f / 100f, n0 = 4f, iso0 = 100, calibrationConstant = 0f)
        val withC = useCase.bv(signal = 100f, t0 = 1f / 100f, n0 = 4f, iso0 = 100, calibrationConstant = 1.5f)
        assertEquals(withoutC + 1.5f, withC, 0.001f)
    }

    @Test
    fun `ev at film iso 400 is 2 stops above bv`() {
        val bv = 10f
        assertEquals(12f, useCase.evAtFilmIso(bv, filmIso = 400), 0.001f)
    }
}
