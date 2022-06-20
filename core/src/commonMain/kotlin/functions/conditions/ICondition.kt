package org.cerion.marketdata.core.functions.conditions


import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.charts.StockChart

interface ICondition {
    val chart: StockChart
    fun eval(list: PriceList): Boolean
}
