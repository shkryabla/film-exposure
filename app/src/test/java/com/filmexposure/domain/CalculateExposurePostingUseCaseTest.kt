package com.filmexposure.domain

import com.filmexposure.domain.model.ExposurePosting
import com.filmexposure.domain.model.MeterPoint
import com.filmexposure.domain.model.PointCategory
import com.filmexposure.domain.usecase.CalculateExposurePostingUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateExposurePostingUseCaseTest {
    private val useCase = CalculateExposurePostingUseCase()

    private fun point(ev: Float) = MeterPoint(x = 0f, y = 0f, ev = ev, category = PointCategory.MID)

    @Test
    fun `single point - posting unavailable, ev equals the point`() {
        val result = useCase(listOf(point(10f)), ExposurePosting.BALANCE)
        assertFalse(result.postingAvailable)
        assertEquals(10f, result.evShooting, 0.001f)
    }

    @Test
    fun `two points - balance is the average`() {
        val result = useCase(listOf(point(8f), point(12f)), ExposurePosting.BALANCE)
        assertTrue(result.postingAvailable)
        assertEquals(10f, result.evShooting, 0.001f)
    }

    @Test
    fun `three points - shadows posting adds 2 stops to darkest`() {
        val result = useCase(listOf(point(11f), point(7f), point(9f)), ExposurePosting.SHADOWS)
        assertEquals(9f, result.evShooting, 0.001f) // 7 + 2
        assertEquals(0f, result.shadowLossStops, 0.001f)
        assertTrue(result.lightLossStops > 0f) // свет (11) уйдёт в перебор
    }
}
