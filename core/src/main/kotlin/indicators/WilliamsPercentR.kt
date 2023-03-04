package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max
import kotlin.math.min

class WilliamsPercentR(period: Int = 14) : IndicatorBase(Indicator.WPR, period) {

    override val name: String = "Williams %R"

    override fun eval(table: OHLCVTable): FloatArray {
        return williamsPercentR(table, getInt(0))
    }

    private fun williamsPercentR(table: OHLCVTable, period: Int): FloatArray {
        val result = FloatArray(table.size)

        //%R = (Highest High - Close)/(Highest High - Lowest Low) * -100
        for (i in table.indices) {
            var h = table.high[i]
            var l = table.low[i]

            val count = ValueArray.maxPeriod(i, period)
            for (j in i - count + 1 until i) {
                h = max(h, table.high[j])
                l = min(l, table.low[j])
            }

            result[i] = (h - table.close[i]) / (h - l) * -100
        }

        return result
    }
}
