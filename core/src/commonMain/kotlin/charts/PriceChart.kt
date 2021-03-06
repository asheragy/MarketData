package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.functions.IOverlay
import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable

class PriceChart(colors: ChartColors = ChartColors()) : StockChart(colors) {
    var candleData = false
    var showPrice = true
    var logScale = false

    private val lineColor: Int
        get() = _colors.primaryBlue

    override fun getDataSets(table: OHLCVTable): List<IDataSet> {
        val result = mutableListOf<IDataSet>()
        val list = if(logScale) table.toLogScale() else table

        if (!showPrice) {
            // Don't add price data
        }
        else if (candleData && canShowCandleData(list))
            result += CandleDataSet(list, "Price")
        else
            result += DataSet(list.close, "Price", lineColor)

        result += getOverlayDataSets(list)
        return result
    }

    override fun getSerializedParams(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // Only save non-default values
        if (logScale) map["logScale"] = logScale.toString()
        if (candleData) map["candleData"] = candleData.toString()
        if (!showPrice) map["showPrice"] = showPrice.toString()

        return map
    }

    override fun setSerializedParams(params: Map<String, String>) {
        logScale = (params["logScale"] ?: "false").toBoolean()
        candleData = (params["candleData"] ?: "false").toBoolean()
        showPrice = (params["showPrice"] ?: "true").toBoolean()
    }

    private fun getOverlayDataSets(table: OHLCVTable): List<DataSet> {
        resetNextColor()
        val result = mutableListOf<DataSet>()

        for (overlay in _overlays) {
            val arr = overlay.eval(table)
            result += getDefaultOverlayDataSets(arr, overlay, lineColor)
        }

        return result
    }

    override val overlays: List<IFunctionEnum>
        get() {
        val overlay = super.overlays
        val priceOverlay = listOf(*PriceOverlay.values())

        return overlay + priceOverlay
    }

    fun addOverlay(overlay: IOverlay) {
        _overlays.add(overlay)
    }

    /**
     * Determines if this chart is able to display candle data, mutual funds on daily interval don't have high/low variation so candles shouldn't be used
     * @return true if this chart can display candle data properly
     */
    fun canShowCandleData(table: OHLCVTable): Boolean {
        // Only daily has this problem with high/low values
        if (table.interval != Interval.DAILY)
            return true

        for (i in table.indices) {
            if (table.high[i] != table.low[i])
                return true
        }

        return false
    }
}
