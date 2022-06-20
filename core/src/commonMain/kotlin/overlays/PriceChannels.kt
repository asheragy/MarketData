package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.*
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.PriceOverlay
import kotlin.math.max

class PriceChannels(period: Int = 20) : PriceOverlayBase(PriceOverlay.CHAN, period) {

    override fun eval(list: PriceList): BandArray {
        return priceChannels(list, getInt(0))
    }

    override val name: String = "Price Channels"

    private fun priceChannels(list: PriceList, period: Int): BandArray {
        val size = list.size
        val upper = FloatArray(size)
        val lower = FloatArray(size)

        upper[0] = list.high[0]
        lower[0] = list.low[0]

        for (i in 1 until size) {
            val p = ValueArray.maxPeriod(i, period)
            val start = max(i - p, 0)
            upper[i] = list.high.max(start, i - 1)
            lower[i] = list.low.min(start, i - 1)
        }

        return BandArray(list.close, upper, lower)
    }
}
