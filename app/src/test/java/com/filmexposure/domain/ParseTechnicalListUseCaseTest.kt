package com.filmexposure.domain

import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ParseTechnicalListUseCaseTest {
    private val useCase = ParseTechnicalListUseCase()

    @Test
    fun `parses and sorts speeds from long to short, B first`() {
        val result = useCase.parseSpeeds("1/125; 1; B; 1/2; 1/4")
        assertEquals(listOf("B", "1", "1/2", "1/4", "1/125"), result)
    }

    @Test
    fun `normalizes 1s suffix and dedupes`() {
        val result = useCase.parseSpeeds("1s; 1; 1/2; 1/2")
        assertEquals(listOf("1", "1/2"), result)
    }

    @Test
    fun `parses bare aperture numbers into f slash notation, widest first`() {
        val result = useCase.parseApertures("5.6; 2.8; 4; f/8")
        assertEquals(listOf("f/2.8", "f/4", "f/5.6", "f/8"), result)
    }

    @Test
    fun `shutterSeconds handles fractions, whole numbers and bulb`() {
        assertEquals(0.5f, useCase.shutterSeconds("1/2")!!, 0.0001f)
        assertEquals(2f, useCase.shutterSeconds("2")!!, 0.0001f)
        assertEquals(null, useCase.shutterSeconds("B"))
    }
}
