package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.functions.types.PriceOverlay

class IchimokuClouds(p1: Int, p2: Int, p3: Int) : PriceOverlayBase(PriceOverlay.CLOUD, p1, p2, p3) {

    constructor() : this(9, 26, 52)

    override fun eval(list: PriceList): PairArray {
        return ichimokuCloud(list, getInt(0), getInt(1), getInt(2))
    }

    override val name: String = "Ichimoku Clouds"

    private fun ichimokuCloud(list: PriceList, conversion: Int, base: Int, span: Int): PairArray {
        val size = list.size
        val spanA = FloatArray(size)
        val spanB = FloatArray(size)

        val highs = list.high
        val lows = list.low

        for (i in span until size) {
            //Conversion Line
            var high = highs.max(i - conversion + 1, i)
            var low = lows.min(i - conversion + 1, i)
            val conversionLine = (high + low) / 2

            //Base line
            high = highs.max(i - base + 1, i)
            low = lows.min(i - base + 1, i)
            val baseLine = (high + low) / 2

            //Leading Span A
            spanA[i] = (conversionLine + baseLine) / 2

            //Leading Span B
            high = highs.max(i - span + 1, i)
            low = lows.min(i - span + 1, i)
            spanB[i] = (high + low) / 2
        }

        return PairArray(spanA, spanB)
    }
}
