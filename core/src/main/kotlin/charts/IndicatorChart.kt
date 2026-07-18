package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.ISimpleOverlay
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.series.*

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

    private fun getOverlayDataSets(arr: Series<*>, ignoreColor: Int): List<DataSet> {
        resetNextColor()
        val result = mutableListOf<DataSet>()
        if (_overlays.isEmpty())
            return result

        val overlayInput = when (arr) {
            is FloatSeries -> arr
            is MACDSeries -> arr.macd
            else -> throw IllegalArgumentException("overlays can only be applied to single-line indicators")
        }

        for (overlay in _overlays) {
            val ol = overlay as ISimpleOverlay

            val temp = ol.eval(overlayInput)
            result += getDefaultOverlayDataSets(temp, ol, ignoreColor)
        }

        return result
    }

    private fun getIndicatorDataSets(arr: Series<*>, indicator: IIndicator): List<DataSet> {
        val label = indicator.toString()

        // TODO look at all uses and see if any colors should be non-defaults (there are some that will)
        return when (arr) {
            is BandSeries -> throw NotImplementedError() // No indicators seem to be using this
            is MACDSeries -> arr.getDataSets(label, label, label, _colors.primaryPurple, _colors.orange, _colors.secondaryBlue)
            is PairSeries -> arr.getDataSets(label, label, _colors.positiveGreen, _colors.negativeRed)
            is org.cerion.marketdata.core.series.FloatSeries -> {
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
