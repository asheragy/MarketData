package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class PringsSpecialK : IndicatorBase(Indicator.SPECIALK) {

    override val name: String = "Pring's Special K"

    override fun eval(table: OHLCVTable): FloatArray {
        return specialK(table)
    }

    private fun specialK(table: OHLCVTable): FloatArray {
        val result = FloatArray(table.size)

        /*
		Special K = 10 Period Simple Moving Average of ROC(10) * 1
	            + 10 Period Simple Moving Average of ROC(15) * 2
	            + 10 Period Simple Moving Average of ROC(20) * 3
	            + 15 Period Simple Moving Average of ROC(30) * 4

	            + 50 Period Simple Moving Average of ROC(40) * 1
	            + 65 Period Simple Moving Average of ROC(65) * 2
	            + 75 Period Simple Moving Average of ROC(75) * 3
	            +100 Period Simple Moving Average of ROC(100)* 4

	            +130 Period Simple Moving Average of ROC(195)* 1
	            +130 Period Simple Moving Average of ROC(265)* 2
	            +130 Period Simple Moving Average of ROC(390)* 3
	            +195 Period Simple Moving Average of ROC(530)* 4
	    */

        //This is just 3 different versions of knowSureThing so it can be calculated easy
        val kst1 = PringsKnowSureThing(10, 15, 20, 30, 10, 10, 10, 15).eval(table)
        val kst2 = PringsKnowSureThing(40, 65, 75, 100, 50, 65, 75, 100).eval(table)
        val kst3 = PringsKnowSureThing(195, 265, 390, 530, 130, 130, 130, 195).eval(table)

        for (i in table.indices)
            result[i] = kst1[i] + kst2[i] + kst3[i]

        return result
    }
}
