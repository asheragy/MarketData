package org.cerion.marketdata.core.overlays


import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.PriceOverlay

class VolumeWeightedMovingAverage(period: Int = 20) : PriceOverlayBase(PriceOverlay.VWMA, period) {

    override val name: String = "Volume Weighted Moving Average"

    override fun eval(list: PriceList): FloatArray {
        val size = list.size
        val period = getInt(0)
        val result = FloatArray(size)

        for (i in 0 until size) {
            val count = ValueArray.maxPeriod(i, period)

            var total = 0f
            var volume = 0f
            for (j in i - count + 1..i) {
                volume += list.volume[j]
                total += list.close[j] * list.volume[j]
            }

            result[i] = total / volume

        }

        return result
    }
}
