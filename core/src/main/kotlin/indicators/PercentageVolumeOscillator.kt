package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.series.MACDSeries
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class PercentageVolumeOscillator(p1: Int, p2: Int, signal: Int) : IndicatorBase(Indicator.PVO, p1, p2, signal) {

    constructor() : this(12, 26, 9)

    override val name: String = "Percentage Volume Oscillator"

    override fun eval(table: OHLCVTable): MACDSeries {
        return PercentagePriceOscillator.getPercentMACD(table.volume, getInt(0), getInt(1), getInt(2))
    }
}
