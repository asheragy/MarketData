package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.OHLCVTable

class ZigZag(percent: Double = 5.0) : PriceOverlayBase(PriceOverlay.ZIGZAG, percent) {

    override fun eval(table: OHLCVTable): FloatArray {
        return zigzag(table, getFloat(0))
    }

    override val name: String = "ZigZag"

    private fun zzPercent(curr: Float, prev: Float): Float {
        return 100 * (curr - prev) / prev
    }

    private fun zigzag(table: OHLCVTable, percent: Float): FloatArray {
        val size = table.size
        val result = FloatArray(table.size)

        // start=-1, down=0, up=1
        var direction = -1
        var currPos = 0

        for (i in table.indices) {
            val high = table.high[i]
            val low = table.low[i]
            val currHigh = table.high[currPos]
            val currLow = table.low[currPos]

            if (direction == -1) {
                if (zzPercent(high, currLow) > percent) {
                    direction = 1
                    currPos = i
                    result[0] = currLow
                } else if (zzPercent(low, currHigh) < -percent) {
                    direction = 0
                    currPos = i
                    result[0] = currHigh
                }
            } else if (direction == 0) {

                if (low < currLow)
                    currPos = i
                else if (zzPercent(high, currLow) > percent) {
                    result[currPos] = currLow
                    direction = 1
                    currPos = i
                }
            } else {
                if (high > currHigh)
                    currPos = i
                else if (zzPercent(low, currHigh) < -percent) {
                    result[currPos] = currHigh
                    direction = 0
                    currPos = i
                }
            }
        }

        // Add current position as the last point
        if (direction == 0)
            result[currPos] = table.low[currPos]
        else if (direction == 1)
            result[currPos] = table.high[currPos]

        // Add last point as the reverse direction
        val last = size - 1
        if (direction == 0)
            result[last] = table.high[last]
        else if (direction == 1)
            result[last] = table.low[last]

        // Fix by adding straight lines to connect points
        var start = 0
        var end: Int
        for (i in 1 until size) {
            if (result[i] != 0f) {
                end = i
                val a = result[start]
                val b = result[end]
                val inc = (b - a) / (end - start)

                for (j in start + 1 until end) {
                    result[j] = result[j - 1] + inc
                }

                start = end
            }
        }

        return result
    }
}
