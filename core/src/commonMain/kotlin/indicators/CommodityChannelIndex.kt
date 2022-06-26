package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.abs

class CommodityChannelIndex(period: Int = 20) : IndicatorBase(Indicator.CCI, period) {

    override val name: String = "Commodity Channel Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return commodityChannelIndex(table, getInt(0))
    }

    private fun commodityChannelIndex(table: OHLCVTable, period: Int): FloatArray {
        val size = table.size
        val result = FloatArray(size)

        val tp = FloatArray(size)
        for (i in 0 until size)
            tp[i] = table[i].tp

        val smaArr = tp.sma(period)

        for (i in 1 until size) {
            val sma = smaArr[i]
            val count = ValueArray.maxPeriod(i, period)

            //Mean deviation is different than standard deviation
            var dev = 0f
            for (j in i - count + 1..i)
                dev += abs(table[j].tp - sma)
            dev /= count

            //CCI = (Typical Price  -  20-period SimpleMovingAverage of TP) / (.015 x Mean Deviation)
            result[i] = (table[i].tp - sma) / (0.015f * dev)
        }

        return result
    }
}
