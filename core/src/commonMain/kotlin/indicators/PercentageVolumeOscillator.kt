package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.MACDArray
import org.cerion.marketdata.core.functions.types.Indicator

class PercentageVolumeOscillator(p1: Int, p2: Int, signal: Int) : IndicatorBase(Indicator.PVO, p1, p2, signal) {

    constructor() : this(12, 26, 9)

    override val name: String = "Percentage Volume Oscillator"

    override fun eval(list: PriceList): MACDArray {
        return PercentagePriceOscillator.getPercentMACD(list.volume, getInt(0), getInt(1), getInt(2))
    }
}
