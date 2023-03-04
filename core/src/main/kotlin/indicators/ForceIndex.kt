package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class ForceIndex(period: Int = 13) : IndicatorBase(Indicator.FORCE_INDEX, period) {

    override val name: String = "Force Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return forceIndex(table, getInt(0))
    }

    private fun forceIndex(table: OHLCVTable, period: Int): FloatArray {
        val close = table.close
        val size = table.size
        val result = FloatArray(size)

        val mult = 2.0f / (1f + period)

        for (i in 1 until size) {
            //Price p = get(i);
            //Price prev = get(i-1);

            val fi = (close[i] - close[i - 1]) * table.volume[i]
            result[i] = (fi - result[i - 1]) * mult + result[i - 1]
            //System.out.println(p.date + "\t" + p.fi);
        }

        return result
    }
}
