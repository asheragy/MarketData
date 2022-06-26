package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class EaseOfMovement(period: Int = 14) : IndicatorBase(Indicator.EMV, period) {

    override val name: String = "Ease of Movement"

    override fun eval(table: OHLCVTable): FloatArray {
        return easeOfMovement(table, getInt(0))
    }

    //1-period EMV
    private fun easeOfMovement(table: OHLCVTable): FloatArray {
        val result = FloatArray(table.size)

        //Distance Moved = ((H + L)/2 - (Prior H + Prior L)/2)
        //Box Ratio = ((V/100,000,000)/(H - L))
        //1-Period EMV = dm / box
        for (i in 1 until table.size) {
            var diff = table.high[i] - table.low[i] //Need to divide by this
            if (diff == 0f)
                diff = 0.01f

            val dm = (table.high[i] + table.low[i]) / 2 - (table.high[i - 1] + table.low[i - 1]) / 2
            val box = table.volume[i] / 100000.0f / diff //Volume is already divided by 1000 so removing 2 digits here
            result[i] = dm / box
            if (box == 0f)
                result[i] = 0f
        }

        return result
    }

    //TODO, double check results again with current arrays
    private fun easeOfMovement(table: OHLCVTable, period: Int): FloatArray {
        val result = FloatArray(table.size)

        //N-Period Ease of Movement = N-Period simple moving average of 1-period EMV
        val emv = easeOfMovement(table)

        for (i in 1 until table.size) {
            val count = ValueArray.maxPeriod(i, period)
            var total = 0f
            for (j in i - count + 1..i)
                total += emv[j]

            result[i] = total / count
        }

        return result
    }
}
