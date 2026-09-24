package com.filmexposure.domain

import com.filmexposure.domain.model.ApexValues
import org.junit.Assert.assertEquals
import org.junit.Test

class ApexValuesTest {
    @Test
    fun `av for f8 is 6`() {
        // Av = 2*log2(N); N=8 -> 2*3 = 6
        assertEquals(6f, ApexValues.av(8f), 0.001f)
    }

    @Test
    fun `tv for 1 over 125s is close to 7`() {
        // Tv = log2(1/t); t=1/128 -> log2(128)=7 (1/125 близко к 1/128)
        assertEquals(6.966f, ApexValues.tv(1f / 125f), 0.01f)
    }

    @Test
    fun `sv for iso 400 is 2`() {
        // Sv = log2(iso/100); iso=400 -> log2(4)=2
        assertEquals(2f, ApexValues.sv(400), 0.001f)
    }

    @Test
    fun `sv for iso 100 is 0`() {
        assertEquals(0f, ApexValues.sv(100), 0.001f)
    }
}
