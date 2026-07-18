package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.series.BandSeries
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max

class PriceChannels(period: Int = 20) : PriceOverlayBase(PriceOverlay.CHAN, period) {

    override fun eval(table: OHLCVTable): BandSeries {
        return priceChannels(table, getInt(0))
    }

    override val name: String = "Price Channels"

    private fun priceChannels(table: OHLCVTable, period: Int): BandSeries {
        val size = table.size
        val upper = FloatSeries(size)
        val lower = FloatSeries(size)

        upper[0] = table.high[0]
        lower[0] = table.low[0]

        for (i in 0 until size) {
            if (i < period) {
                upper[i] = Float.NaN
                lower[i] = Float.NaN
                continue
            }

            val start = max(i - period, 0)
            upper[i] = table.high.max(start, i - 1)
            lower[i] = table.low.min(start, i - 1)
        }

        return BandSeries(upper, lower, table.close)
    }
}
