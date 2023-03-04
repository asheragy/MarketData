package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class ChaikinOscillator(p1: Int, p2: Int) : IndicatorBase(Indicator.CO, p1, p2) {

    constructor() : this(3, 10)

    override val name: String = "Chaikin Oscillator"

    override fun eval(table: OHLCVTable): FloatArray {
        return chaikinOscillator(table, getInt(0), getInt(1))
    }

    private fun chaikinOscillator(table: OHLCVTable, p1: Int, p2: Int): FloatArray {
        val result = FloatArray(table.size)

        val adl = AccumulationDistributionLine().eval(table)
        val ema1 = adl.ema(p1)
        val ema2 = adl.ema(p2)

        for (i in table.indices)
            result[i] = ema1[i] - ema2[i]

        return result
    }

}
