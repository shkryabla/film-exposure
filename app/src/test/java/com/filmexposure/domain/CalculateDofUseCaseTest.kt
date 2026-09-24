package com.filmexposure.domain

import com.filmexposure.domain.usecase.CalculateDofUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateDofUseCaseTest {
    private val useCase = CalculateDofUseCase()

    @Test
    fun `focus distance beyond hyperfocal gives infinite far`() {
        // f=50mm, N=8, c=0.029mm (35mm) -> H ~ 50 + 50*50/(8*0.029) ~ 10827mm
        val result = useCase(focalLength = 50f, aperture = 8f, coc = 0.029f, focusDistance = 50_000f)
        assertTrue(result.far.isInfinite())
        assertEquals(result.hyperfocal, result.near, 1f)
    }

    @Test
    fun `focus distance well below hyperfocal gives finite dof`() {
        val result = useCase(focalLength = 50f, aperture = 8f, coc = 0.029f, focusDistance = 2000f)
        assertTrue(result.far.isFinite())
        assertTrue(result.near < 2000f)
        assertTrue(result.far > 2000f)
        assertEquals(result.total, result.far - result.near, 0.01f)
    }
}
