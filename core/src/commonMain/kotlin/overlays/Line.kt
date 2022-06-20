package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Overlay

class Line(slope: Double = 1.0) : OverlayBase<FloatArray>(Overlay.LINE, slope) {

    override val name: String = "Line"

    override fun eval(arr: FloatArray): FloatArray {
        val slope = getFloat(0)

        val result = FloatArray(arr.size)
        result[0] = arr[0]
        for (i in 1 until arr.size) {
            result[i] = result[i - 1] + slope
        }

        return result
    }
}
