package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.series.BandSeries
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.indicators.AverageTrueRange
import org.cerion.marketdata.core.model.OHLCVTable

class KeltnerChannels(period: Int, multiplier: Double, atr: Int) : PriceOverlayBase(PriceOverlay.KC, period, multiplier, atr) {

    constructor() : this(20, 2.0, 10)

    override val name: String = "Keltner Channels"

    override fun eval(table: OHLCVTable): BandSeries {
        val emaPeriod = getInt(0)
        val multiplier = getFloat(1)
        val atrPeriod = getInt(2)

        val ema = ExpMovingAverage(emaPeriod).eval(table)
        //Middle Line: 20-day exponential moving average
        //Upper Channel Line: 20-day ExpMovingAverage + (2 x ATR(10))
        //Lower Channel Line: 20-day ExpMovingAverage - (2 x ATR(10))

        val upper = FloatSeries(table.size)
        val lower = FloatSeries(table.size)
        val atr = AverageTrueRange(atrPeriod).eval(table)

        for (i in 1 until table.size) {
            upper[i] = ema[i] + multiplier * atr[i]
            lower[i] = ema[i] - multiplier * atr[i]
        }

        return BandSeries(upper, lower, table.close)
    }
}
