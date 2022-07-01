package org.cerion.marketdata.core

import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.utils.CSVParser
import kotlin.math.abs

open class TestBase {

    fun assertEquals(expected: Number, actual: Float, message: String? = null) =
            assertEquals(expected, actual, 0.005, message)

    fun assertEquals(expected: Number, actual: Float, delta: Double, message: String? = null) {
        val diff = abs(expected.toDouble() - actual)
        if (diff > delta)
            kotlin.test.assertEquals(expected, actual, "expected=$expected, actual=$actual, diff=$diff - $message")
    }

    protected fun runPriceTest(block: suspend (priceList: OHLCVTable) -> Unit) = Utils.runAsync {
        if (!isInitialized()) {
            val data = Utils.readResourceFileAsync("sp500_2000-2015.csv").await()
            priceList = PriceList("^GSPC", CSVParser.getPricesFromTable(data))
        }

        block(priceList)
    }

    companion object {
        fun isInitialized() = ::priceList.isInitialized
        private lateinit var priceList: PriceList
    }
}