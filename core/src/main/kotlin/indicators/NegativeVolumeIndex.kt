package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class NegativeVolumeIndex : IndicatorBase(Indicator.NVI) {

    override val name: String = "Negative Volume Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return negativeVolumeIndex(table)
    }

    private fun negativeVolumeIndex(table: OHLCVTable): FloatArray {
        val result = FloatArray(table.size)

        result[0] = 1000f
        for (i in 1 until table.size) {
            if (table.volume[i] < table.volume[i - 1])
                result[i] = result[i - 1] + table.close.roc(i, 1)
            else
                result[i] = result[i - 1]

        }

        return result
    }
}
