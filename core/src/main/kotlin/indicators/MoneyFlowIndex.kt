package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class MoneyFlowIndex(period: Int = 14) : IndicatorBase(Indicator.MFI, period) {

    override val name: String = "Money Flow Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return moneyFlowIndex(table, getInt(0))
    }

    private fun moneyFlowIndex(table: OHLCVTable, period: Int): FloatArray {
        val result = FloatArray(table.size)

        //Typical Price = (High + Low + Close)/3
        //Raw Money Flow = Typical Price x Volume
        //Money Flow Ratio = (14-period Positive Money Flow)/(14-period Negative Money Flow)
        //Money Flow Index = 100 - 100/(1 + Money Flow Ratio)
        for (i in period until table.size) {
            var posflow = 0f
            var negflow = 0f
            for (j in i - period + 1..i) {
                if (table[j].tp > table[j - 1].tp)
                    posflow += table[j].tp * table.volume[j]
                else
                    negflow += table[j].tp * table.volume[j]
            }

            val ratio = posflow / negflow
            result[i] = 100 - 100 / (1 + ratio)
        }

        return result
    }
}
