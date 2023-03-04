package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max
import kotlin.math.sqrt

class UlcerIndex(period: Int = 14) : IndicatorBase(Indicator.ULCER_INDEX, period) {

    override val name: String = "Ulcer Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return eval(table.close)
    }

    fun eval(arr: FloatArray): FloatArray {
        val period = getInt(0)
        val size = arr.size
        val result = FloatArray(size)

        //Percent-Drawdown = ((Close - 14-period Max Close)/14-period Max Close) x 100
        //Squared Average = (14-perod Sum of Percent-Drawdown Squared)/14
        //Ulcer Index = Square Root of Squared Average

        //Set Percent Drawdown
        val percentD = FloatArray(size)
        for (i in 0 until size) {
            var max = 0f //Max close
            val count = ValueArray.maxPeriod(i, period)
            for (j in i - count + 1..i)
                max = max(max, arr[j])

            percentD[i] = (arr[i] - max) / max * 100
        }

        for (i in 0 until size) {
            var avg = 0f
            val count = ValueArray.maxPeriod(i, period)
            for (j in i - count + 1..i)
                avg += percentD[j] * percentD[j] //Sum of squared

            avg /= period.toFloat()
            result[i] = sqrt(avg.toDouble()).toFloat()
        }

        return result
    }
}
