package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class UltimateOscillatorTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val arr = UltimateOscillator(7, 14, 28).eval(it)

        assertEquals(0.0, arr.first, "uo 0")
        assertEquals(0.0, arr[1], "uo 1")
        assertEquals(50.21, arr[28], "uo 28")
        assertEquals(63.82, arr[arr.size - 2], "uo last-1")
        assertEquals(56.02, arr.last, "uo last")
    }
}