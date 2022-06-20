package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator

class Stochastic() : IndicatorBase(Indicator.STOCH, 14, 3, 3) {

    // TODO allow 1-3 parameters
    // This one is a special case as we do allow vararg but more than 4 is not allowed so probably seperate constructors needed
    // TODO learn more how this function works if all arugments are not specified, need unit tests prior
    constructor(vararg params: Number) : this() {
        setParams(*params)
    }

    override val name: String = "Stochastic Oscillator"

    override fun eval(list: PriceList): FloatArray {
        return stochastic(list, getInt(0), getInt(1), getInt(2))
    }

    private fun stochastic(list: PriceList, K: Int): FloatArray {
        val result = FloatArray(list.size)
        val highs = list.high
        val lows = list.low

        //K = period
        for (i in list.indices) {
            val period = ValueArray.maxPeriod(i, K)

            val high = highs.max(i - period + 1, i)
            val low = lows.min(i - period + 1, i)

            //K = (Current Close - Lowest Low)/(Highest High - Lowest Low) * 100
            result[i] = (list.close[i] - low) / (high - low) * 100
        }

        return result
    }

    private fun stochastic(list: PriceList, K: Int, D: Int): FloatArray {
        val result = stochastic(list, K)
        return result.sma(D)
    }

    private fun stochastic(list: PriceList, K: Int, fastD: Int, slowD: Int): FloatArray {
        val result = stochastic(list, K, fastD)
        return result.sma(slowD)
    }

}
