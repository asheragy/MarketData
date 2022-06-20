package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import kotlin.math.max
import kotlin.math.min

class UltimateOscillator(p1: Int, p2: Int, p3: Int) : IndicatorBase(Indicator.UO, p1, p2, p3) {

    constructor() : this(7, 14, 28)

    override val name: String = "Ultimate Oscillator"

    override fun eval(list: PriceList): FloatArray {
        return ultimateOscillator(list, getInt(0), getInt(1), getInt(2))
    }

    private fun ultimateOscillator(list: PriceList, p1: Int, p2: Int, p3: Int): FloatArray {
        val size = list.size
        val result = FloatArray(size)

        val bp = kotlin.FloatArray(size)
        for (i in 1 until size)
            bp[i] = list.close[i] - min(list.low[i], list.close[i - 1])

        val average = Array(size) { kotlin.FloatArray(3) }

        //First Period
        for (i in p1 until size) {
            var bpsum = 0f
            var trsum = 0f
            for (j in i - p1 + 1..i) {
                bpsum += bp[j]
                trsum += list.tr(j)
            }
            average[i][0] = bpsum / trsum

            if (trsum == 0f)
                average[i][0] = 0f
        }

        //Second Period
        for (i in p2 until size) {
            var bpsum = 0f
            var trsum = 0f
            for (j in i - p2 + 1..i) {
                bpsum += bp[j]
                trsum += list.tr(j)
            }
            average[i][1] = bpsum / trsum
        }

        for (i in p3 until size) {
            var bpsum = 0f
            var trsum = 0f
            for (j in i - p3 + 1..i) {
                bpsum += bp[j]
                trsum += list.tr(j)
            }
            average[i][2] = bpsum / trsum
        }

        //Parameters should be ordered lowest to highest, but just in-case
        val max = max(max(p1, p2), p3)
        for (i in max until size) {
            val avg1 = average[i][0]
            val avg2 = average[i][1]
            val avg3 = average[i][2]
            result[i] = 100 * (4 * avg1 + 2 * avg2 + avg3) / (4 + 2 + 1)
        }

        return result
    }

}
