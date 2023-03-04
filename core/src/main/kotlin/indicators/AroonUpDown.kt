package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class AroonUpDown(period: Int = 25) : IndicatorBase(Indicator.AROON, period) {

    override val name: String = "Aroon Up/Down"

    override fun eval(table: OHLCVTable): PairArray {
        return aroon(table, getInt(0))
    }

    private fun aroon(table: OHLCVTable, period: Int): PairArray {
        val size = table.size
        val up = FloatArray(size)
        val down = FloatArray(size)
        //Aroon Up = 100 x (25 - Days Since 25-day High)/25
        //Aroon Down = 100 x (25 - Days Since 25-day Low)/25
        //Aroon Oscillator = Aroon-Up  -  Aroon-Down

        for (i in period - 1 until size) {
            val high = i - table.close.maxPos(i - period + 1, i) + 1
            val low = i - table.close.minPos(i - period + 1, i) + 1

            up[i] = (100 * (period - high) / period).toFloat()
            down[i] = (100 * (period - low) / period).toFloat()
        }

        return PairArray(up, down)
    }
}
