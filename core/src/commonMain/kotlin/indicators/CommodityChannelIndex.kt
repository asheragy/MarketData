package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import kotlin.math.abs

class CommodityChannelIndex(period: Int = 20) : IndicatorBase(Indicator.CCI, period) {

    override val name: String = "Commodity Channel Index"

    override fun eval(list: PriceList): FloatArray {
        return commodityChannelIndex(list, getInt(0))
    }

    private fun commodityChannelIndex(list: PriceList, period: Int): FloatArray {
        val size = list.size
        val result = FloatArray(size)

        val tp = FloatArray(size)
        for (i in 0 until size)
            tp[i] = list.tp(i)

        val smaArr = tp.sma(period)

        for (i in 1 until size) {
            val sma = smaArr[i]
            val count = ValueArray.maxPeriod(i, period)

            //Mean deviation is different than standard deviation
            var dev = 0f
            for (j in i - count + 1..i)
                dev += abs(list[j].tp() - sma)
            dev /= count

            //CCI = (Typical Price  -  20-period SimpleMovingAverage of TP) / (.015 x Mean Deviation)
            result[i] = (list.tp(i) - sma) / (0.015f * dev)
        }

        return result
    }
}
