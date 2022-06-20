package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import kotlin.math.max
import kotlin.math.min

class WilliamsPercentR(period: Int = 14) : IndicatorBase(Indicator.WPR, period) {

    override val name: String = "Williams %R"

    override fun eval(list: PriceList): FloatArray {
        return williamsPercentR(list, getInt(0))
    }

    private fun williamsPercentR(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)

        //%R = (Highest High - Close)/(Highest High - Lowest Low) * -100
        for (i in list.indices) {
            var h = list.high[i]
            var l = list.low[i]

            val count = ValueArray.maxPeriod(i, period)
            for (j in i - count + 1 until i) {
                h = max(h, list.high[j])
                l = min(l, list.low[j])
            }

            result[i] = (h - list.close[i]) / (h - l) * -100
        }

        return result
    }
}
