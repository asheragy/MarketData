package org.cerion.marketdata.core.model


import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class OHLCVTableTest : TestBase() {

    @Test
    fun averageYearlyGain() = runPriceTest {
        val gain = it.averageYearlyGain()
        assertEquals(0.0215, gain, 0.0001)
    }

    @Test
    fun generateSeries() {
        val list = OHLCVTable.generateSeries(500)
        assertEquals(500, list.size.toFloat())
        assertEquals(102.0f, list[0].close)
        assertEquals(319.43283f, list.last().close)
    }
}