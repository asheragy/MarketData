package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class AverageTrueRange(period: Int = 14) : IndicatorBase(Indicator.ATR, period) {

    override val name: String = "Average True Range"

    override fun eval(table: OHLCVTable): FloatSeries {
        return averageTrueRange(table, getInt(0))
    }

    private fun averageTrueRange(table: OHLCVTable, period: Int): FloatSeries {
        val result = FloatSeries(table.size)

        //Current ATR = [(Prior ATR x 13) + Current TR] / 14
        result[0] = table.tr(0)
        for (i in 1 until table.size)
            result[i] = (result[i - 1] * (period - 1) + table.tr(i)) / period

        return result
    }
}
