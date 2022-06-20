package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator

class PriceMomentumOscillator(p1: Int, p2: Int) : IndicatorBase(Indicator.PMO, p1, p2) {

    constructor() : this(35, 20)

    override val name: String = "Price Momentum Oscillator"

    override fun eval(list: PriceList): FloatArray {
        return priceMomentumOscillator(list, getInt(0), getInt(1))
    }

    private fun priceMomentumOscillator(list: PriceList, p1: Int, p2: Int): FloatArray {
        val result = FloatArray(list.size)

        val m1 = 2.0f / p1
        val m2 = 2.0f / p2
        var ema = 0f

        for (i in 1 until list.size) {
            val roc = list.roc(i, 1)

            ema = roc * m1 + ema * (1 - m1)

            val e = ema * 10
            result[i] = (e - result[i - 1]) * m2 + result[i - 1]
        }

        return result
    }

}
