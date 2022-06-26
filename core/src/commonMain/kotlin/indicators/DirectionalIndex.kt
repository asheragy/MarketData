package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max

class DirectionalIndex(period: Int = 14) : IndicatorBase(Indicator.DI, period) {

    override val name: String = "Directional Index"

    override fun eval(table: OHLCVTable): PairArray {
        return directionalIndex(table, getInt(0))
    }

    private fun directionalIndex(table: OHLCVTable, period: Int): PairArray {
        val size = table.size
        val mDI = FloatArray(size) //-DI
        val pDI = FloatArray(size) //+DI

        val trdm = Array(size) { kotlin.FloatArray(2) } //+DM / -DM

        for (i in 1 until size) {
            val prev = i - 1

            //TODO, add DM function to PriceList so this can be calculated directly
            if (table.high[i] - table.high[prev] > table.low[prev] - table.low[i])
                trdm[i][0] = max(table.high[i] - table.high[prev], 0f)

            if (table.low[prev] - table.low[i] > table.high[i] - table.high[prev])
                trdm[i][1] = max(table.low[prev] - table.low[i], 0f)
        }

        val trdm14 = Array(size) { kotlin.FloatArray(3) } //TR14 / +DM14 / -DM14

        for (i in 1 until size) {
            trdm14[i][0] = trdm14[i - 1][0] - trdm14[i - 1][0] / period + table.tr(i)
            trdm14[i][1] = trdm14[i - 1][1] - trdm14[i - 1][1] / period + trdm[i][0]
            trdm14[i][2] = trdm14[i - 1][2] - trdm14[i - 1][2] / period + trdm[i][1]

            pDI[i] = 100 * (trdm14[i][1] / trdm14[i][0])
            mDI[i] = 100 * (trdm14[i][2] / trdm14[i][0])
        }

        return PairArray(pDI, mDI)
    }
}
