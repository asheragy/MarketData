package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class PercentageVolumeOscillatorTest : TestBase() {

    @Test
    fun macd_temp() = runPriceTest {
        // TODO temp to quick check PPO and PVO

        val arr = PercentageVolumeOscillator(12, 26, 9).eval(it)
        assertEquals(-10.86, arr.last, "pvo last")
    }
}