package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.series.BandSeries
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.Overlay

class BollingerBands(period: Int, stddev: Double) : OverlayBase<BandSeries>(Overlay.BB, period, stddev) {

    constructor() : this(20, 2.0)

    override val name: String = "Bollinger Bands"

    override fun eval(arr: FloatSeries): BandSeries {
        return eval(arr, getInt(0), getFloat(1))
    }

    private fun eval(arr: FloatSeries, period: Int, multiplier: Float): BandSeries {
        val sma = arr.sma(getInt(0))
        val std = arr.std(period, sma)

        val upper = FloatSeries(arr.size)
        val lower = FloatSeries(arr.size)

        for (i in 1 until arr.size) {
            upper[i] = sma[i] + multiplier * std[i]
            lower[i] = sma[i] - multiplier * std[i]
        }

        return BandSeries(arr, upper, lower)
    }
}
