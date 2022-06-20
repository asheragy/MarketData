package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class MassIndex(period: Int = 25) : IndicatorBase(Indicator.MASS_INDEX, period) {

    override val name: String = "Mass Index"

    override fun eval(list: PriceList): FloatArray {
        return massIndex(list, getInt(0))
    }

    private fun massIndex(list: PriceList, period: Int): FloatArray {
        val size = list.size
        val result = FloatArray(size)

        //Single ExpMovingAverage = 9-period exponential moving average (ExpMovingAverage) of the high-low differential
        //Double ExpMovingAverage = 9-period ExpMovingAverage of the 9-period ExpMovingAverage of the high-low differential
        //ExpMovingAverage Ratio = Single ExpMovingAverage divided by Double ExpMovingAverage
        //Mass Index = 25-period sum of the ExpMovingAverage Ratio

        val highLowDiff = FloatArray(size)
        for (i in 0 until size) {
            highLowDiff[i] = list.high[i] - list.low[i]
        }

        val ema = highLowDiff.ema(9)
        val ema2 = ema.ema(9)
        val emaRatio = FloatArray(size)

        //X period sum
        for (i in 0 until size) {
            emaRatio[i] = ema[i] / ema2[i]

            //int max = ValueArray.maxPeriod(i, period);
            if (i >= period - 1) {
                result[i] = emaRatio.sum(i - period + 1, i)
            } else if (i == 0) {
                result[i] = period.toFloat()
            } else {
                // Normalize, average ema ratio is 1
                // X period sum of ratio is average of the period size
                // Anything value before the period size will be normalized so the average stays the same
                val mult = period / (1.0f + i)
                result[i] = mult * emaRatio.sum(0, i)
            }
        }

        return result
    }
}
