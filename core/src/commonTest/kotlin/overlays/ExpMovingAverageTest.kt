package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class ExpMovingAverageTest : TestBase() {

    @Test
    fun ema_tests() = runPriceTest {
        // Not verified online although many other indicators use this so its tested indirectly
        var ema = ExpMovingAverage().eval(it)
        assertEquals(it.close.first, ema.first)
        assertEquals(2054.47, ema.last)

        ema = ExpMovingAverage(123).eval(it)
        assertEquals(it.close.first, ema.first)
        assertEquals(2047.90, ema.last)
    }
}