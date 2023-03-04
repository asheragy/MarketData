package org.cerion.marketdata.core.indicators


import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.sqrt

class SharpeRatio(period: Int, riskFreeRate: Double) : IndicatorBase(Indicator.SHARPE, period, riskFreeRate) {

    // Default 0.75 estimate of 3-month US treasury
    constructor() : this(10, 0.75)

    override val name: String = "Sharpe Ratio"

    override fun eval(table: OHLCVTable): FloatArray {
        val years = getInt(0)
        val multiplier: Int

        multiplier = when (table.interval) {
            Interval.DAILY -> 252
            Interval.WEEKLY -> 52
            Interval.MONTHLY -> 12
            Interval.QUARTERLY -> 4
            Interval.YEARLY -> 1
        }

        val change = table.close.percentChange
        val riskFree = getFloat(1) / 100 / multiplier

        for (i in 1 until change.size) {
            change[i] = change[i] - riskFree
        }

        val avg = change.sma(years * multiplier)
        val std = change.std(years * multiplier)

        val result = FloatArray(table.size)
        for (i in table.indices) {
            if (i >= multiplier) {
                result[i] = avg[i] / std[i]
                result[i] *= sqrt(multiplier.toFloat())
            } else
                result[i] = Float.NaN
        }

        return result
    }
}
