package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class NegativeVolumeIndex : IndicatorBase(Indicator.NVI) {

    override val name: String = "Negative Volume Index"

    override fun eval(list: PriceList): FloatArray {
        return negativeVolumeIndex(list)
    }

    private fun negativeVolumeIndex(list: PriceList): FloatArray {
        val result = FloatArray(list.size)

        result[0] = 1000f
        for (i in 1 until list.size) {
            if (list.volume[i] < list.volume[i - 1])
                result[i] = result[i - 1] + list.roc(i, 1)
            else
                result[i] = result[i - 1]

        }

        return result
    }
}
