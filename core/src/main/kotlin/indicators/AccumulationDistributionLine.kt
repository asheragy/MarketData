package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class AccumulationDistributionLine : IndicatorBase(Indicator.ADL) {

    override val name: String = "Accumulation Distribution Line"

    override fun eval(table: OHLCVTable): FloatArray {
        return accumulationDistributionLine(table)
    }

    private fun accumulationDistributionLine(table: OHLCVTable): FloatArray {
        val result = FloatArray(table.size)

        result[0] = 0f
        for (i in 1 until table.size) {
            //ADL = Previous ADL + Current Period's Money Flow Volume
            result[i] = result[i - 1] + table[i].mfv
        }

        return result
    }
}
