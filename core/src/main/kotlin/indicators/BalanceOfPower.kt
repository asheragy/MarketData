package org.cerion.marketdata.core.indicators

import com.tictactec.ta.lib.Core
import com.tictactec.ta.lib.MInteger
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.toFloatArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.indicators.IndicatorBase
import org.cerion.marketdata.core.model.OHLCVTable

class BalanceOfPower(period: Int = 14) : IndicatorBase(Indicator.BOP, period) {

    override val name: String = "Balance of Power"

    override fun eval(table: OHLCVTable): FloatArray {
        val outIdx = MInteger()
        val outElement = MInteger()
        val result = DoubleArray(table.size)
        taLib.bop(0, table.size - 1,
            table.open.toFloatArray(),
            table.high.toFloatArray(),
            table.low.toFloatArray(),
            table.close.toFloatArray(),
            outIdx, outElement, result)

        return result.map { it.toFloat() }.toFloatArray()
    }
}