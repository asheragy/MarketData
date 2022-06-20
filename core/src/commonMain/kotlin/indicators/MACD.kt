package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.MACDArray
import org.cerion.marketdata.core.functions.types.Indicator

class MACD(p1: Int, p2: Int, signal: Int) : IndicatorBase(Indicator.MACD, p1, p2, signal) {

    constructor() : this(12, 26, 9)

    override val name: String = "MACD"

    override fun eval(list: PriceList): MACDArray {
        return macd(list, getInt(0), getInt(1), getInt(2))
    }

    private fun macd(list: PriceList, p1: Int, p2: Int, signal: Int): MACDArray {
        val result = MACDArray(list.size, signal)
        val ema1 = list.close.ema(p1)
        val ema2 = list.close.ema(p2)

        for (i in list.indices)
            result[i] = ema1[i] - ema2[i]

        return result
    }
}
