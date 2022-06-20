package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.ISimpleOverlay

class VolumeChart(colors: ChartColors = ChartColors()) : StockChart(colors) {
    var logScale = false

    private val barColor: Int
        get() = _colors.volumneBlue

    override fun getDataSets(priceList: PriceList): List<IDataSet> {
        val result = mutableListOf<IDataSet>()
        val volume = if(logScale) priceList.toLogScale().volume else priceList.volume

        val data = DataSet(volume, "Volume", barColor)
        data.lineType = LineType.BAR
        result += data
        result += getOverlayDataSets(volume)

        return result
    }

    override fun getSerializedParams(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        // Only save non-default values
        if (logScale) map["logScale"] = logScale.toString()

        return map
    }

    override fun setSerializedParams(params: Map<String, String>) {
        logScale = (params["logScale"] ?: "false").toBoolean()
    }

    private fun getOverlayDataSets(volume: FloatArray): List<IDataSet> {
        resetNextColor()
        val result = mutableListOf<IDataSet>()

        for (overlay in _overlays) {
            val ol = overlay as ISimpleOverlay
            val arr = ol.eval(volume)
            result += getDefaultOverlayDataSets(arr, overlay, barColor)
        }

        return result
    }
}
