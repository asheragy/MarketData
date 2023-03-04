package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.functions.IFunction
import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.IOverlay
import org.cerion.marketdata.core.model.OHLCVTable

class IndicatorCondition
/**
 * Condition that [indicator1] is [above/below] [indicator2]
 * @param indicator1 indicator to compare from
 * @param condition condition above/below
 * @param indicator2 indicator to compare to
 */
(private val indicator1: IFunction, private val condition: Condition, private val indicator2: IFunction) : ICondition {

    override val chart: StockChart
        get() {
            if (indicator1 is IIndicator) {
                val chart = IndicatorChart(indicator1)
                chart.add(indicator2 as IIndicator)

                return chart
            } else {
                val chart = PriceChart()
                chart.addOverlay(indicator1 as IOverlay)
                chart.addOverlay(indicator2 as IOverlay)

                return chart
            }
        }

    init {
        // Only valid to compare two float arrays with each other
        if (indicator1.resultType != FloatArray::class)
            throw IllegalArgumentException("indicator1 must return type FloatArray")
        if (indicator2.resultType != FloatArray::class)
            throw IllegalArgumentException("indicator2 must return type FloatArray")

        if (condition === Condition.INSIDE)
            throw IllegalArgumentException("condition must be above/below")
    }

    override fun eval(table: OHLCVTable): Boolean {
        val arr1 = indicator1.eval(table) as FloatArray
        val arr2 = indicator2.eval(table) as FloatArray

        return if (condition === Condition.ABOVE)
            arr1.last > arr2.last
        else
            arr1.last < arr2.last
    }

    override fun toString(): String {
        return indicator1.toString() + " " + condition.toString().lowercase() + " " + indicator2.toString()
    }
}
