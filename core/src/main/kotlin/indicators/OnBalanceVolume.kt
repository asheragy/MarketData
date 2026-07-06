package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class OnBalanceVolume : IndicatorBase(Indicator.OBV) {

    override val name: String = "On Balance Volume"

    override fun eval(table: OHLCVTable): FloatSeries {
        return onBalanceVolume(table)
    }

    private fun onBalanceVolume(table: OHLCVTable): FloatSeries {
        val close = table.close
        val volume = table.volume
        val result = FloatSeries(table.size)

        result[0] = 0f
        for (i in 1 until table.size) {
            if (close[i] > close[i - 1])
                result[i] = result[i - 1] + volume[i]
            else if (close[i] < close[i - 1])
                result[i] = result[i - 1] - volume[i]
            else
                result[i] = result[i - 1]
        }

        return result
    }
}
