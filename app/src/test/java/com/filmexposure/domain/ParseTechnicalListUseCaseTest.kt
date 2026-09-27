package com.filmexposure.domain
import com.filmexposure.domain.usecase.ParseTechnicalListUseCase
import org.junit.Assert.assertEquals
import org.junit.Test
class ParseTechnicalListUseCaseTest {
    private val useCase = ParseTechnicalListUseCase()
    @Test
    fun `shutterSeconds handles fractions, whole numbers and bulb`() {
        assertEquals(0.5f, useCase.shutterSeconds("1/2")!!, 0.0001f)
        assertEquals(1f, useCase.shutterSeconds("1")!!, 0.0001f)
        assertEquals(null, useCase.shutterSeconds("B"))
    }
    @Test
    fun `apertureValue parses f-slash notation`() {
        assertEquals(2.8f, useCase.apertureValue("f/2.8")!!, 0.0001f)
        assertEquals(16f, useCase.apertureValue("f/16")!!, 0.0001f)
    }
}
