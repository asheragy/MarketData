package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class OnBalanceVolume : IndicatorBase(Indicator.OBV) {

    override val name: String = "On Balance Volume"

    override fun eval(list: PriceList): FloatArray {
        return onBalanceVolume(list)
    }

    private fun onBalanceVolume(list: PriceList): FloatArray {
        val close = list.close
        val volume = list.volume
        val result = FloatArray(list.size)

        result[0] = 0f
        for (i in 1 until list.size) {
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
