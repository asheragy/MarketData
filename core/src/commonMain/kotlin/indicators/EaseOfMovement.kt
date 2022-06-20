package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator

class EaseOfMovement(period: Int = 14) : IndicatorBase(Indicator.EMV, period) {

    override val name: String = "Ease of Movement"

    override fun eval(list: PriceList): FloatArray {
        return easeOfMovement(list, getInt(0))
    }

    //1-period EMV
    private fun easeOfMovement(list: PriceList): FloatArray {
        val result = FloatArray(list.size)

        //Distance Moved = ((H + L)/2 - (Prior H + Prior L)/2)
        //Box Ratio = ((V/100,000,000)/(H - L))
        //1-Period EMV = dm / box
        for (i in 1 until list.size) {
            var diff = list.high[i] - list.low[i] //Need to divide by this
            if (diff == 0f)
                diff = 0.01f

            val dm = (list.high[i] + list.low[i]) / 2 - (list.high[i - 1] + list.low[i - 1]) / 2
            val box = list.volume[i] / 100000.0f / diff //Volume is already divided by 1000 so removing 2 digits here
            result[i] = dm / box
            if (box == 0f)
                result[i] = 0f
        }

        return result
    }

    //TODO, double check results again with current arrays
    private fun easeOfMovement(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)

        //N-Period Ease of Movement = N-Period simple moving average of 1-period EMV
        val emv = easeOfMovement(list)

        for (i in 1 until list.size) {
            val count = ValueArray.maxPeriod(i, period)
            var total = 0f
            for (j in i - count + 1..i)
                total += emv[j]

            result[i] = total / count
        }

        return result
    }
}
