package org.cerion.marketdata.core.functions.conditions


import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.model.OHLCVTable

interface ICondition {
    val chart: StockChart
    fun eval(table: OHLCVTable): Boolean
}
