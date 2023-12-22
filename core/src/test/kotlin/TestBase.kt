package org.cerion.marketdata.core

import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.utils.CSVParser
import kotlin.math.abs

open class TestBase {

    fun assertNotSet(actual: Float) {
        kotlin.test.assertEquals(Float.NaN, actual)
    }
    fun assertEquals(expected: Number, actual: Float, message: String? = null) = assertEquals(expected, actual, 0.005, message)

    fun assertEquals(expected: Number, actual: Float, delta: Double, message: String? = null) {
        if (expected == Float.NaN || actual.isNaN())
            throw RuntimeException("Unexpected float value")

        val diff = abs(expected.toDouble() - actual)
        if (diff > delta)
            kotlin.test.assertEquals(expected, actual, "expected=$expected, actual=$actual, diff=$diff - $message")
    }

    @Deprecated("Use table directly")
    protected fun runPriceTest(block: suspend (priceList: OHLCVTable) -> Unit) = Utils.runAsync {
        block(table)
    }

    companion object {
        fun getTable(filename: String): OHLCVTable {
            val data = Utils.readResourceFile(filename)
            return OHLCVTable("unknown", CSVParser.getPricesFromTable(data))
        }

        val table = OHLCVTable("^GSPC", CSVParser.getPricesFromTable(Utils.readResourceFile("sp500_2000-2015.csv")))
        val table22 = OHLCVTable("^GSPC", CSVParser.getPricesFromTable(Utils.readResourceFile("sp500_2010-2022.csv")))
    }
}