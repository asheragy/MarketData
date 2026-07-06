package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.Overlay

class Line(slope: Double = 1.0) : OverlayBase<FloatSeries>(Overlay.LINE, slope) {

    override val name: String = "Line"

    override fun eval(arr: FloatSeries): FloatSeries {
        val slope = getFloat(0)

        val result = FloatSeries(arr.size)
        result[0] = arr[0]
        for (i in 1 until arr.size) {
            result[i] = result[i - 1] + slope
        }

        return result
    }
}
