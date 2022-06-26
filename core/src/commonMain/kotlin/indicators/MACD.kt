package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.MACDArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class MACD(p1: Int, p2: Int, signal: Int) : IndicatorBase(Indicator.MACD, p1, p2, signal) {

    constructor() : this(12, 26, 9)

    override val name: String = "MACD"

    override fun eval(table: OHLCVTable): MACDArray {
        return macd(table, getInt(0), getInt(1), getInt(2))
    }

    private fun macd(table: OHLCVTable, p1: Int, p2: Int, signal: Int): MACDArray {
        val result = MACDArray(table.size, signal)
        val ema1 = table.close.ema(p1)
        val ema2 = table.close.ema(p2)

        for (i in table.indices)
            result[i] = ema1[i] - ema2[i]

        return result
    }
}
