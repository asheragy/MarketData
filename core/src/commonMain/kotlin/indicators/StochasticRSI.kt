package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator

class StochasticRSI(period: Int = 14) : IndicatorBase(Indicator.STOCHRSI, period) {

    override val name: String = "Stochastic RSI"

    override fun eval(list: PriceList): FloatArray {
        return stochasticRSI(list, getInt(0))
    }

    private fun stochasticRSI(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)
        val rsi_arr = RSI(period).eval(list)

        for (i in list.indices) {
            var high = rsi_arr[i]
            var low = rsi_arr[i]

            val count = ValueArray.maxPeriod(i, period)
            for (j in i - count + 1 until i) {
                val rsi = rsi_arr[j]
                if (rsi > high)
                    high = rsi
                if (rsi < low)
                    low = rsi
            }

            //StochRSI = (RSI - Lowest Low RSI) / (Highest High RSI - Lowest Low RSI)
            if (high == low)
                result[i] = 1f
            else
                result[i] = (rsi_arr[i] - low) / (high - low)
        }

        return result
    }

}
