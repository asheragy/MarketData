package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class ChaikinMoneyFlow(period: Int = 20) : IndicatorBase(Indicator.CMF, period) {

    override val name: String = "Chaikin Money Flow"

    override fun eval(table: OHLCVTable): FloatArray {
        return chaikinMoneyFlow(table, getInt(0))
    }

    private fun chaikinMoneyFlow(table: OHLCVTable, period: Int): FloatArray {
        val result = FloatArray(table.size)

        //CMF = N-period Sum of Money Flow Volume / N period Sum of Volume
        for (i in table.indices) {
            val start = i - ValueArray.maxPeriod(i, period) + 1
            var mfvolume = 0f
            var volume = 0f
            for (j in start..i) {
                mfvolume += table[j].mfv
                volume += table.volume[j]
            }

            result[i] = mfvolume / volume
        }

        return result
    }
}
