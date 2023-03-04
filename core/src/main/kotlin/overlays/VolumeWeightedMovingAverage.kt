package org.cerion.marketdata.core.overlays


import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.OHLCVTable

class VolumeWeightedMovingAverage(period: Int = 20) : PriceOverlayBase(PriceOverlay.VWMA, period) {

    override val name: String = "Volume Weighted Moving Average"

    override fun eval(table: OHLCVTable): FloatArray {
        val size = table.size
        val period = getInt(0)
        val result = FloatArray(size)

        for (i in 0 until size) {
            if (i < period - 1) {
                result[i] = Float.NaN
                continue
            }

            var total = 0f
            var volume = 0f
            for (j in i - period + 1..i) {
                volume += table.volume[j]
                total += table.close[j] * table.volume[j]
            }

            result[i] = total / volume

        }

        return result
    }
}
