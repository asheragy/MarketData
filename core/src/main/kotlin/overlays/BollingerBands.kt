package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Overlay

class BollingerBands(period: Int, stddev: Double) : OverlayBase<BandArray>(Overlay.BB, period, stddev) {

    constructor() : this(20, 2.0)

    override val name: String = "Bollinger Bands"

    override fun eval(arr: FloatArray): BandArray {
        return eval(arr, getInt(0), getFloat(1))
    }

    private fun eval(arr: FloatArray, period: Int, multiplier: Float): BandArray {
        val sma = arr.sma(getInt(0))
        val std = arr.std(period, sma)

        val upper = FloatArray(arr.size)
        val lower = FloatArray(arr.size)

        for (i in 1 until arr.size) {
            upper[i] = sma[i] + multiplier * std[i]
            lower[i] = sma[i] - multiplier * std[i]
        }

        return BandArray(arr, upper, lower)
    }
}
