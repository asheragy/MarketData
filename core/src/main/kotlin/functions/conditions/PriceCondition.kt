package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.series.BandSeries
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.functions.IPriceOverlay
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable

class PriceCondition(private val condition: Condition, private val overlay: IPriceOverlay) : ICondition {

    override val chart: StockChart
        get() {
            val chart = PriceChart()
            chart.addOverlay(overlay)
            chart.candleData = false

            return chart
        }

    init {
        if (condition === Condition.INSIDE && overlay.resultType != BandSeries::class)
            throw IllegalArgumentException("condition")
    }

    override fun eval(table: OHLCVTable): Boolean {
        val arr = overlay.eval(table)

        return if (arr is FloatSeries) {
            evalFloatArray(arr, table.last())
        } else if (arr is BandSeries) {
            evalBandArray(arr)
        } else
            throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return "Price " + condition.toString().lowercase() + " " + overlay.toString()
    }

    private fun evalBandArray(arr: BandSeries): Boolean {
        val percent = arr.percent(arr.size - 1)

        if (condition === Condition.ABOVE && percent > 1)
            return true
        if (condition === Condition.BELOW && percent < 0)
            return true

        return condition === Condition.INSIDE && percent < 1 && percent > 0

    }

    private fun evalFloatArray(arr: FloatSeries, last: OHLCVRow): Boolean {
        val v = arr.last

        if (condition === Condition.ABOVE && last.close > v)
            return true

        return condition === Condition.BELOW && last.close < v

    }

}
