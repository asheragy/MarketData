package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class TRIX(period: Int = 15) : IndicatorBase(Indicator.TRIX, period) {

    override val name: String = "TRIX"

    override fun eval(table: OHLCVTable): FloatArray {
        return trix(table, getInt(0))
    }

    private fun trix(table: OHLCVTable, period: Int): FloatArray {
        val result = FloatArray(table.size)

        val ema1 = table.close.ema(period)
        val ema2 = ema1.ema(period)
        val ema3 = ema2.ema(period)

        for (i in 1 until table.size) {
            //1-Day percent change in Triple ExpMovingAverage
            result[i] = (ema3[i] - ema3[i - 1]) / ema3[i - 1] * 100
        }

        return result
    }
}
