package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class TRIX(period: Int = 15) : IndicatorBase(Indicator.TRIX, period) {

    override val name: String = "TRIX"

    override fun eval(list: PriceList): FloatArray {
        return trix(list, getInt(0))
    }

    private fun trix(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)

        val ema1 = list.close.ema(period)
        val ema2 = ema1.ema(period)
        val ema3 = ema2.ema(period)

        for (i in 1 until list.size) {
            //1-Day percent change in Triple ExpMovingAverage
            result[i] = (ema3[i] - ema3[i - 1]) / ema3[i - 1] * 100
        }

        return result
    }
}
