package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class KeltnerChannelsTest : TestBase() {

    @Test
    fun keltnerChannels_defaults() = runPriceTest {
        val arr = KeltnerChannels().eval(it)

        val last = arr.size - 1
        // TODO verify values online, just doing these pre-refactor
        assertEquals(4.62, arr.bandwidth(last), "bandwidth")
        assertEquals(2007.02, arr.lower(last), "lower")
        assertEquals(2101.92, arr.upper(last), "upper")
        assertEquals(0.39, arr.percent(last), "percent")
        assertEquals(2054.47, arr.mid(last), "mid")
    }
}