package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.series.Series
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.abs

class AverageDirectionalIndex(period: Int = 14) : IndicatorBase(Indicator.ADX, period) {

    override val name: String = "Average Directional Index"

    override fun eval(table: OHLCVTable): FloatSeries {
        return averageDirectionalIndex(table, getInt(0))
    }

    private fun averageDirectionalIndex(table: OHLCVTable, period: Int): FloatSeries {
        val size = table.size
        val result = FloatSeries(size)
        val di = DirectionalIndex(period).eval(table)

        for (i in 1 until size) {
            val count = Series.maxPeriod(i, period)
            //Directional Movement Index (DX) equals the absolute value of +DI minus -DI divided by the sum of +DI and -DI.
            val diff = di.pos(i) - di.neg(i)
            val sum = di.pos(i) + di.neg(i)

            val dx = 100 * (abs(diff) / sum)
            result[i] = (result[i - 1] * (count - 1) + dx) / count
        }

        return result
    }

}
