package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class PercentagePriceOscillatorTest : TestBase() {

    @Test
    fun macd_temp() = runPriceTest {
        // TODO temp to quick check PPO and PVO

        val arr = PercentagePriceOscillator(12, 26, 9).eval(it)
        assertEquals(-0.08, arr.last, "ppo last")
    }
}