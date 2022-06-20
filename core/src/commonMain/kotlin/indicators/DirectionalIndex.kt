package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.functions.types.Indicator
import kotlin.math.max

class DirectionalIndex(period: Int = 14) : IndicatorBase(Indicator.DI, period) {

    override val name: String = "Directional Index"

    override fun eval(list: PriceList): PairArray {
        return directionalIndex(list, getInt(0))
    }

    private fun directionalIndex(list: PriceList, period: Int): PairArray {
        val size = list.size
        val mDI = FloatArray(size) //-DI
        val pDI = FloatArray(size) //+DI

        val trdm = Array(size) { kotlin.FloatArray(2) } //+DM / -DM

        for (i in 1 until size) {
            val prev = i - 1

            //TODO, add DM function to PriceList so this can be calculated directly
            if (list.high[i] - list.high[prev] > list.low[prev] - list.low[i])
                trdm[i][0] = max(list.high[i] - list.high[prev], 0f)

            if (list.low[prev] - list.low[i] > list.high[i] - list.high[prev])
                trdm[i][1] = max(list.low[prev] - list.low[i], 0f)
        }

        val trdm14 = Array(size) { kotlin.FloatArray(3) } //TR14 / +DM14 / -DM14

        for (i in 1 until size) {
            trdm14[i][0] = trdm14[i - 1][0] - trdm14[i - 1][0] / period + list.tr(i)
            trdm14[i][1] = trdm14[i - 1][1] - trdm14[i - 1][1] / period + trdm[i][0]
            trdm14[i][2] = trdm14[i - 1][2] - trdm14[i - 1][2] / period + trdm[i][1]

            pDI[i] = 100 * (trdm14[i][1] / trdm14[i][0])
            mDI[i] = 100 * (trdm14[i][2] / trdm14[i][0])
        }

        return PairArray(pDI, mDI)
    }
}
