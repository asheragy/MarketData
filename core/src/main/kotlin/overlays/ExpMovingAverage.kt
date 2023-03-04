package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Overlay

class ExpMovingAverage(period: Int = 20) : OverlayBase<FloatArray>(Overlay.EMA, period) {

    override val name: String = "Exp. Moving Average"

    override fun eval(arr: FloatArray): FloatArray {
        val period = getInt(0)
        val result = FloatArray(arr.size)

        if (arr.size > 0) {
            val mult = 2.0f / (1f + period) //ExpMovingAverage multiplier

            result[0] = arr[0] //initialize with first value
            for (i in 1 until arr.size)
                result[i] = (arr[i] - result[i - 1]) * mult + result[i - 1]
        }

        return result
    }
}
