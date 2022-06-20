package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class CandleDataSetTest : TestBase() {

    @Test
    fun sizeOffsetByOne() = runPriceTest {
        val data = CandleDataSet(it, "")

        assertEquals(it.size - 1, data.size, "size should be 1 less")
        assertEquals(it.close[1], data.getClose(0), 0.0001, "invalid close value at position 0")
        assertEquals(it.high[1], data.getHigh(0), 0.0001, "invalid high value at position 0")
        assertEquals(it.low[1], data.getLow(0), 0.0001, "invalid low value at position 0")
        assertEquals(it.open[1], data.getOpen(0), 0.0001, "invalid open value at position 0")
    }
}