package org.cerion.marketdata.core.model


import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.Utils
import org.cerion.marketdata.core.utils.CSVParser
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

    @Test
    fun beta() = Utils.runAsync {
        var data = Utils.readResourceFileAsync("sp500_2010-2022.csv").await()
        val index = OHLCVTable("^GSPC", CSVParser.getPricesFromTable(data))
        data = Utils.readResourceFileAsync("AMZN_2010-2022.csv").await()
        val amzn = OHLCVTable("^GSPC", CSVParser.getPricesFromTable(data))

        val beta = amzn.beta(index, 200)
        assertEquals(1.65, beta.last)
    }
}
