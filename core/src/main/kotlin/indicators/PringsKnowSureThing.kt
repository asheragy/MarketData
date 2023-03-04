package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class PringsKnowSureThing(p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int)
    : IndicatorBase(Indicator.KST, p1, p2, p3, p4, p5, p6, p7, p8) {

    constructor() : this(10, 15, 20, 30, 10, 10, 10, 15)

    override val name: String = "Pring's Know Sure Thing"

    override fun eval(table: OHLCVTable): FloatArray {
        return knowSureThing(table.close, getInt(0), getInt(1), getInt(2), getInt(3),
                getInt(4), getInt(5), getInt(6), getInt(7))
    }

    /*
    Short-term Daily = KST(10,15,20,30,10,10,10,15)
    Medium-term Weekly = KST(10,13,15,20,10,13,15,20)
    Long-term Monthly = KST(9,12,18,24,6,6,6,9)
    Default signal is 9 period SimpleMovingAverage (not ExpMovingAverage)
    */
    private fun knowSureThing(close: FloatArray, roc1: Int, roc2: Int, roc3: Int, roc4: Int, sma1: Int, sma2: Int, sma3: Int, sma4: Int): FloatArray {
        val size = close.size
        val result = FloatArray(size)

        var r1 = FloatArray(size)
        var r2 = FloatArray(size)
        var r3 = FloatArray(size)
        var r4 = FloatArray(size)

        /*
		RCMA1 = 10-Period SimpleMovingAverage of 10-Period Rate-of-Change
		RCMA2 = 10-Period SimpleMovingAverage of 15-Period Rate-of-Change
		RCMA3 = 10-Period SimpleMovingAverage of 20-Period Rate-of-Change
		RCMA4 = 15-Period SimpleMovingAverage of 30-Period Rate-of-Change
		KST = (RCMA1 x 1) + (RCMA2 x 2) + (RCMA3 x 3) + (RCMA4 x 4)
		*/
        for (i in 0 until size) {
            r1[i] = close.roc(i, roc1)
            r2[i] = close.roc(i, roc2)
            r3[i] = close.roc(i, roc3)
            r4[i] = close.roc(i, roc4)
        }

        //Apply SimpleMovingAverage to arrays
        r1 = r1.sma(sma1)
        r2 = r2.sma(sma2)
        r3 = r3.sma(sma3)
        r4 = r4.sma(sma4)

        for (i in 0 until size)
            result[i] = r1[i] + r2[i] * 2 + r3[i] * 3 + r4[i] * 4

        return result
    }
}
