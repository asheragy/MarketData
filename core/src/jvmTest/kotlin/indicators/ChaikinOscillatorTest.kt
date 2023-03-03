package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class ChaikinOscillatorTest : TestBase() {

    @Test
    fun chaikinOscillator_test_defaults() = runPriceTest {
        val arr = ChaikinOscillator().eval(it)

        // TODO verify values online, just doing these pre-refactor
        assertEquals(0.0, arr.first, "first")
        assertEquals(-298935.10, arr[1], 0.05, "position 1")
        assertEquals(-265271.78, arr[2], 0.05, "position 2")
        assertEquals(550272.0, arr.last, "last")
    }
}