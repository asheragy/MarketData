package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.MACDArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class PercentagePriceOscillator(p1: Int, p2: Int, signal: Int) : IndicatorBase(Indicator.PPO, p1, p2, signal) {

    constructor() : this(12, 26, 9)

    override val name: String = "Percentage Price Oscillator"

    override fun eval(table: OHLCVTable): MACDArray {
        //Percentage version of MACD
        return getPercentMACD(table.close, getInt(0), getInt(1), getInt(2))
    }

    companion object {

        // Shared with PVO
        fun getPercentMACD(arr: FloatArray, p1: Int, p2: Int, signal: Int): MACDArray {
            val result = MACDArray(arr.size, signal)
            val ema1 = arr.ema(p1)
            val ema2 = arr.ema(p2)

            for (i in 0 until arr.size)
                result[i] = 100 * (ema1[i] - ema2[i]) / ema2[i]

            return result
        }
    }

}
