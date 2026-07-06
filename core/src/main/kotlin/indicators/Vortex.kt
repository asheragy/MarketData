package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.series.PairSeries
import org.cerion.marketdata.core.series.Series
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.abs

class Vortex(period: Int = 14) : IndicatorBase(Indicator.VORTEX, period) {

    override val name: String = "Vortex"

    override fun eval(table: OHLCVTable): PairSeries {
        return vortex(table, getInt(0))
    }

    private fun vortex(table: OHLCVTable, period: Int): PairSeries {
        val size = table.size
        val posVI = FloatSeries(size)
        val negVI = FloatSeries(size)

        val vm = Array(size) { FloatArray(2) } // +VM/-VM

        for (i in 1 until size) {
            vm[i][0] = abs(table.high[i] - table.low[i - 1])
            vm[i][1] = abs(table.low[i] - table.high[i - 1])
        }

        // Start at 1 since that is the average value
        posVI[0] = 1f
        negVI[0] = 1f

        for (i in 1 until size) {
            val count = Series.maxPeriod(i, period)
            var vip = 0f
            var vin = 0f
            var tr = 0f
            for (j in i - count + 1..i) {
                vip += vm[j][0]
                vin += vm[j][1]
                tr += table.tr(j)
            }

            posVI[i] = vip / tr
            negVI[i] = vin / tr
        }

        return PairSeries(posVI, negVI)
    }

}
