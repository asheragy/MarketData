package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class MoneyFlowIndex(period: Int = 14) : IndicatorBase(Indicator.MFI, period) {

    override val name: String = "Money Flow Index"

    override fun eval(list: PriceList): FloatArray {
        return moneyFlowIndex(list, getInt(0))
    }

    private fun moneyFlowIndex(list: PriceList, period: Int): FloatArray {
        val result = FloatArray(list.size)

        //Typical Price = (High + Low + Close)/3
        //Raw Money Flow = Typical Price x Volume
        //Money Flow Ratio = (14-period Positive Money Flow)/(14-period Negative Money Flow)
        //Money Flow Index = 100 - 100/(1 + Money Flow Ratio)
        for (i in period until list.size) {
            var posflow = 0f
            var negflow = 0f
            for (j in i - period + 1..i) {
                if (list.tp(j) > list.tp(j - 1))
                    posflow += list.tp(j) * list.volume[j]
                else
                    negflow += list.tp(j) * list.volume[j]
            }

            val ratio = posflow / negflow
            result[i] = 100 - 100 / (1 + ratio)
        }

        return result
    }
}
