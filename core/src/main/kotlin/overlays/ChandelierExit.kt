package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.indicators.AverageTrueRange
import org.cerion.marketdata.core.model.OHLCVTable

class ChandelierExit(period: Int, multiplier: Double) : PriceOverlayBase(PriceOverlay.CEXIT, period, multiplier) {

    constructor() : this(22, 3.0)

    override fun eval(table: OHLCVTable): PairArray {
        return chandelierExit(table, getInt(0), getFloat(1))
    }

    override val name: String = "Chandelier Exit"

    private fun chandelierExit(table: OHLCVTable, period: Int, multiplier: Float): PairArray {
        val size = table.size

        val high = FloatArray(size)
        val low = FloatArray(size)
        val atr = AverageTrueRange(period).eval(table)

        for (i in 0 until size) {
            if (i < period - 1) {
                high[i] = Float.NaN
                low[i] = Float.NaN
                continue
            }

            val h = table.high.max(i - period + 1, i) // highest high
            val l = table.low.min(i - period + 1, i) // lowest low

            high[i] = h - atr[i] * multiplier
            low[i] = l + atr[i] * multiplier
        }

        return PairArray(high, low)
    }
}
