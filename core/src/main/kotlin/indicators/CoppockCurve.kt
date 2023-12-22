package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max

class CoppockCurve(roc1: Int = 14, roc2: Int = 11, period: Int = 10) : IndicatorBase(Indicator.CCURVE, roc1, roc2, period) {

    override val name = "Coppock Curve"

    override fun eval(table: OHLCVTable): FloatArray {
        val roc1 = table.close.roc(getInt(0))
        val roc2 = table.close.roc(getInt(1))
        val period = getInt(2)
        val result = FloatArray(table.size)

        val start = max(getInt(0), getInt(1)) + period - 1
        for(i in 0 until table.size) {
            if (i < start)
                result[i] = Float.NaN
            else {
                var sum = 0f
                var weight = 0
                var weightSum = 0
                for(j in i-period+1 .. i) {
                    weight++
                    weightSum += weight
                    sum += weight * (roc1[j] + roc2[j])
                }

                result[i] = sum / weightSum
            }
        }

        return result
    }
}