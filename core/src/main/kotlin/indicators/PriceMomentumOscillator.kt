package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class PriceMomentumOscillator(p1: Int, p2: Int) : IndicatorBase(Indicator.PMO, p1, p2) {

    constructor() : this(35, 20)

    override val name: String = "Price Momentum Oscillator"

    override fun eval(table: OHLCVTable): FloatArray {
        return priceMomentumOscillator(table, getInt(0), getInt(1))
    }

    private fun priceMomentumOscillator(table: OHLCVTable, p1: Int, p2: Int): FloatArray {
        val result = FloatArray(table.size)

        val m1 = 2.0f / p1
        val m2 = 2.0f / p2
        var ema = 0f

        for (i in 1 until table.size) {
            val roc = table.close.roc(i, 1)

            ema = roc * m1 + ema * (1 - m1)

            val e = ema * 10
            result[i] = (e - result[i - 1]) * m2 + result[i - 1]
        }

        return result
    }

}
