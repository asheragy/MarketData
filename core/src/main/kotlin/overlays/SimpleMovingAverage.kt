package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.series.Series
import org.cerion.marketdata.core.functions.types.Overlay

class SimpleMovingAverage(period: Int = 50) : OverlayBase<FloatSeries>(Overlay.SMA, period) {

    override val name: String = "Simple Moving Average"

    override fun eval(arr: FloatSeries): FloatSeries {
        val period = getInt(0)
        val result = FloatSeries(arr.size)

        for (i in 0 until arr.size) {
            //Take average of first i array elements when count is less than period size
            val count = Series.maxPeriod(i, period)

            var total = 0f
            for (j in i - count + 1..i)
                total += arr[j]

            result[i] = total / count
        }

        return result
    }
}
