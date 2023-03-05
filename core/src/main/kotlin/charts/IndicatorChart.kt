package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.arrays.*
import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.ISimpleOverlay
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable

class IndicatorChart(indicator: IIndicator, colors: ChartColors = ChartColors()) : StockChart(colors) {

    private val extra = ArrayList<IIndicator>()

    var indicator: IIndicator = indicator
        set(value) {
            clearOverlays()
            field = value
        }

    val id: Indicator
        get() = indicator.id

    override fun getSerializedParams(): Map<String, String> {
        return mapOf(Pair("indicator", indicator.serialize()))
    }

    override fun setSerializedParams(params: Map<String, String>) {
        // No extra fields to set
    }

    fun add(extraIndicator: IIndicator) {
        if (indicator.id !== extraIndicator.id)
            throw IllegalArgumentException("must be type " + indicator.id)

        extra.add(extraIndicator)
    }

    override fun getDataSets(table: OHLCVTable): List<IDataSet> {
        val result = mutableListOf<IDataSet>()

        val arr = indicator.eval(table)
        result += getIndicatorDataSets(arr, indicator)

        // TODO set color on these
        for (indicator in extra) {
            val va = indicator.eval(table)
            result += getIndicatorDataSets(va, indicator)
        }

        // Pass color of first data set to be ignored for any overlays
        result += getOverlayDataSets(arr, result[0].color)
        return result
    }

    private fun getOverlayDataSets(arr: ValueArray<*>, ignoreColor: Int): List<DataSet> {
        resetNextColor()
        val result = mutableListOf<DataSet>()

        for (overlay in _overlays) {
            val ol = overlay as ISimpleOverlay

            val temp = ol.eval(arr as org.cerion.marketdata.core.arrays.FloatArray)
            result += getDefaultOverlayDataSets(temp, ol, ignoreColor)
        }

        return result
    }

    private fun getIndicatorDataSets(arr: ValueArray<*>, indicator: IIndicator): List<DataSet> {
        val label = indicator.toString()

        // TODO look at all uses and see if any colors should be non-defaults (there are some that will)
        return when (arr) {
            is BandArray -> throw NotImplementedError() // No indicators seem to be using this
            is MACDArray -> arr.getDataSets(label, label, label, _colors.primaryPurple, _colors.orange, _colors.secondaryBlue)
            is PairArray -> arr.getDataSets(label, label, _colors.positiveGreen, _colors.negativeRed)
            is org.cerion.marketdata.core.arrays.FloatArray -> {
                // TODO add more special cases
                var color = _colors.primary
                if (indicator.id == Indicator.RSI)
                    color = _colors.primaryPurple

                listOf(arr.toDataSet(label, color))
            }
            else -> throw NotImplementedError()
        }
    }
}
