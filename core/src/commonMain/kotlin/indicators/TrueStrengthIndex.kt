package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.abs

class TrueStrengthIndex(p1: Int, p2: Int) : IndicatorBase(Indicator.TSI, p1, p2) {

    constructor() : this(25, 13)

    override val name: String = "True Strength Index"

    override fun eval(table: OHLCVTable): FloatArray {
        return trueStrengthIndex(table, getInt(0), getInt(1))
    }

    private fun trueStrengthIndex(table: OHLCVTable, p1: Int, p2: Int): FloatArray {
        val size = table.size
        val result = FloatArray(table.size)
        /*
    	-Double Smoothed PC
    	PC = Current Price less Prior Price
    	First Smoothing = 25-period ExpMovingAverage of PC
    	Second Smoothing = 13-period ExpMovingAverage of 25-period ExpMovingAverage of PC

    	-Double Smoothed Absolute PC
    	Absolute Price Change |PC| = Absolute Value of Current Price less Prior Price
    	First Smoothing = 25-period ExpMovingAverage of |PC|
    	Second Smoothing = 13-period ExpMovingAverage of 25-period ExpMovingAverage of |PC|

    	TSI = 100 x (Double Smoothed PC / Double Smoothed Absolute PC)
    	*/

        var PC = FloatArray(size)
        var PCabs = FloatArray(size)
        for (i in 1 until size) {
            PC[i] = table.close[i] - table.close[i - 1]
            PCabs[i] = abs(PC[i])
        }

        // Smoothing
        PC = PC.ema(p1).ema(p2)
        PCabs = PCabs.ema(p1).ema(p2)

        // Let first 2 values be 0
        for (i in 2 until size) {
            result[i] = 100 * (PC[i] / PCabs[i])
        }

        return result
    }
}
