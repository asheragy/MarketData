package org.cerion.marketdata.core


import kotlin.test.Test
import kotlin.test.assertEquals

class PriceListTest : TestBase() {

    @Test
    fun averageYearlyGain() = runPriceTest {
        val gain = it.averageYearlyGain()
        assertEquals(0.0215, gain, 0.0001)
    }

    @Test
    fun generateSeries() {
        val list = PriceList.generateSeries(500)
        assertEquals(500, list.size)
        assertEquals(102.0f, list[0].close)
        assertEquals(319.43283f, list.last().close)
    }
}
