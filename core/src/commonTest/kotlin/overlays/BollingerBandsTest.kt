package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class BollingerBandsTest : TestBase() {

    @Test
    fun bollingerBands_defaults() = runPriceTest {
        val bands = BollingerBands()
        val arr=  bands.eval(it)

        // TODO need to verify online
        assertEquals(4.32, arr.bandwidth(arr.size - 1), "bandwidth")
        assertEquals(2006.05, arr.lower(arr.size - 1), "lower")
        assertEquals(2050.38, arr.mid(arr.size - 1), "mid")
        assertEquals(0.43, arr.percent(arr.size - 1), "percent")
        assertEquals(2094.71, arr.upper(arr.size - 1), "upper")
    }
}