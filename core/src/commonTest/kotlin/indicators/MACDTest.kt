package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class MACDTest : TestBase() {

    @Test
    fun macd_defaults() = runPriceTest {
        val arr = MACD().eval(it)

        val last = arr.size - 1
        assertEquals(-1.69, arr[last], "last")
        assertEquals(1.84, arr.hist(last), "hist")
        assertEquals(-3.54, arr.signal(last), "signal")
    }
}