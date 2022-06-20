package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Overlay

class SimpleMovingAverage(period: Int = 50) : OverlayBase<FloatArray>(Overlay.SMA, period) {

    override val name: String = "Simple Moving Average"

    override fun eval(arr: FloatArray): FloatArray {
        val period = getInt(0)
        val result = FloatArray(arr.size)

        for (i in 0 until arr.size) {
            //Take average of first i array elements when count is less than period size
            val count = ValueArray.maxPeriod(i, period)

            var total = 0f
            for (j in i - count + 1..i)
                total += arr[j]

            result[i] = total / count
        }

        return result
    }
}
