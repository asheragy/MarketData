package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.functions.IFunction
import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.IOverlay

class ValueIndicatorCondition
/**
 * Condition that [value] is [above/below/inside] [indicator]
 * @param value Value to check against indicator
 * @param condition condition
 * @param indicator indicator to compare to
 */
(private val value: Float, private val condition: Condition, private val indicator: IFunction) : ICondition {

    override val chart: StockChart
        get() {
            if (indicator is IIndicator) {
                return IndicatorChart(indicator)
            } else {
                val chart = PriceChart()
                chart.addOverlay(indicator as IOverlay)
                return chart
            }
        }

    init {
        if (condition === Condition.INSIDE && indicator.resultType != BandArray::class)
            throw IllegalArgumentException(Condition.INSIDE.toString() + " must be applied to " + BandArray::class.simpleName)
    }

    override fun eval(list: PriceList): Boolean {

        // Value vs Indicator
        val va = indicator.eval(list)
        if (va is BandArray) {
            val last = va.size - 1
            when (condition) {
                Condition.ABOVE -> return value > va.upper(last)
                Condition.BELOW -> return value < va.lower(last)
                Condition.INSIDE -> return value > va.lower(last) && value < va.upper(last)
            }
        }

        // FloatArray
        val arr = indicator.eval(list) as FloatArray
        return if (condition === Condition.ABOVE)
            value > arr.last
        else
            value < arr.last
    }

    override fun toString(): String {
        return value.toString() + " " + condition.toString().toLowerCase() + " " + indicator.toString()
    }
}
