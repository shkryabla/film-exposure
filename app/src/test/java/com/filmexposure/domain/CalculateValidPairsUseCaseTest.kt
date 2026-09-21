package com.filmexposure.domain

import com.filmexposure.domain.usecase.CalculateValidPairsUseCase
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateValidPairsUseCaseTest {
    private val useCase = CalculateValidPairsUseCase(ParseTechnicalListUseCase())

    @Test
    fun `reciprocal pairs f8-125 and f11-60 give the same Ev`() {
        // Av(8)=6, Tv(1/125)=~6.966 -> Ev~12.966; Av(11)=~6.92, Tv(1/60)=~5.907 -> Ev~12.83
        val result = useCase(
            targetEv = 12.9f,
            apertureLabels = listOf("f/8", "f/11"),
            shutterLabels = listOf("1/125", "1/60"),
            toleranceEv = 0.2f,
        )
        assertTrue(result.any { it.aperture == "f/8" && it.shutter == "1/125" })
        assertTrue(result.any { it.aperture == "f/11" && it.shutter == "1/60" })
    }

    @Test
    fun `bulb is excluded from results`() {
        val result = useCase(
            targetEv = 10f,
            apertureLabels = listOf("f/8"),
            shutterLabels = listOf("B", "1/125"),
            toleranceEv = 5f,
        )
        assertTrue(result.none { it.shutter == "B" })
    }

    @Test
    fun `out of range Ev yields no pairs`() {
        val result = useCase(
            targetEv = 100f,
            apertureLabels = listOf("f/8"),
            shutterLabels = listOf("1/125"),
        )
        assertEquals(0, result.size)
    }
}
