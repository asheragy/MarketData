package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class PriceChannelsTest : TestBase() {

    @Test
    fun priceChannels_defaults() {
        val arr = PriceChannels().eval(table)

        arr[0].apply {
            assertNotSet(upper)
            assertNotSet(lower)
        }
        arr[19].apply {
            assertNotSet(upper)
            assertNotSet(lower)
        }

        assertEquals(1350.14, arr.lower(20), "priceChannels 20")

        val last = arr.last
        assertEquals(2104.27, last.upper, "priceChannels upper last")
        assertEquals(1993.26, last.lower, "priceChannels lower last")
        assertEquals(2048.77, last.mid, "mid last")
        assertEquals(5.42, last.bandwidth, "bandwidth last")
        assertEquals(0.46, last.percent, "percent last")
    }

    @Test
    fun priceChannels_100() {
        val arr = PriceChannels(100).eval(table)
        assertEquals(2116.48, arr.upper(table.size - 1), "priceChannels upper last with different parameters")
        assertEquals(1867.01, arr.lower(table.size - 1), "priceChannels lower last with different parameters")
    }
}