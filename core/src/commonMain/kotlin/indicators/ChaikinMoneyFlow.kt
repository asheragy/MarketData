package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator

class ChaikinMoneyFlow(period: Int = 20) : IndicatorBase(Indicator.CMF, period) {

    override val name: String = "Chaikin Money Flow"

    override fun eval(list: PriceList): FloatArray {
        return chaikinMoneyFlow(list, getInt(0))
    }

    private fun chaikinMoneyFlow(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)

        //CMF = N-period Sum of Money Flow Volume / N period Sum of Volume
        for (i in list.indices) {
            val start = i - ValueArray.maxPeriod(i, period) + 1
            var mfvolume = 0f
            var volume = 0f
            for (j in start..i) {
                mfvolume += list.mfv(j)
                volume += list.volume[j]
            }

            result[i] = mfvolume / volume
        }

        return result
    }
}
