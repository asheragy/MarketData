package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class OnBalanceVolume : IndicatorBase(Indicator.OBV) {

    override val name: String = "On Balance Volume"

    override fun eval(table: OHLCVTable): FloatArray {
        return onBalanceVolume(table)
    }

    private fun onBalanceVolume(table: OHLCVTable): FloatArray {
        val close = table.close
        val volume = table.volume
        val result = FloatArray(table.size)

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
