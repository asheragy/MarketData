package org.cerion.marketdata.core.overlays


import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.Overlay

class LinearRegressionLine : OverlayBase<FloatSeries>(Overlay.LINREG) {

    override val name: String = "Linear Regression Line"

    override fun eval(arr: FloatSeries): FloatSeries {

        val pos = arr.size - 1
        val result = FloatSeries(arr.size)

        val ab = arr.getLinearRegressionEquation(0, pos)
        val slope = ab[1]
        result[0] = ab[0]

        for (i in 1 until arr.size) {
            result[i] = result[i - 1] + slope
        }

        return result
    }
}
