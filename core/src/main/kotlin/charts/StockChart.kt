package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.PairArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.FunctionBase
import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.IOverlay
import org.cerion.marketdata.core.functions.ISimpleOverlay
import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.functions.types.Overlay
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.ParabolicSAR
import java.time.LocalDate

abstract class StockChart(protected val _colors: ChartColors) {

    protected var _overlays: MutableList<IOverlay> = ArrayList()
    private var _nextColor = 0

    open val overlays: List<IFunctionEnum>
        get() = Overlay.values().toList()

    val overlayCount: Int
        get() = _overlays.size

    abstract fun getDataSets(table: OHLCVTable): List<IDataSet>

    fun getDates(table: OHLCVTable): Array<LocalDate> = table.dates.sliceArray(1 until table.dates.size)

    fun copy(): StockChart = deserialize(serialize(), _colors)

    fun addOverlay(overlay: ISimpleOverlay): StockChart {
        _overlays.add(overlay)
        return this
    }

    fun clearOverlays() {
        _overlays.clear()
    }

    fun getOverlay(position: Int): IOverlay = _overlays[position]

    protected abstract fun getSerializedParams(): Map<String, String>
    protected abstract fun setSerializedParams(params: Map<String, String>)

    private fun getNextColor(ignoreColor: Int): Int {
        val color = _colors.getOverlayColor(_nextColor++)

        if (color == ignoreColor && color != 0) // Ignore zero for unit tests that don't set color values
            return getNextColor((ignoreColor))

        // For volume teal is too close to default so replace with purple
        if (this is VolumeChart && color == _colors.teal)
            return _colors.primaryPurple

        return color
    }

    protected fun resetNextColor() {
        _nextColor = 0
    }

    protected fun getDefaultOverlayDataSets(arr: ValueArray<*>, overlay: IOverlay, ignoreColor: Int): List<DataSet> {
        val label = overlay.toString()

        return when (arr) {
            is BandArray -> arr.getDataSets(label, label, getNextColor(ignoreColor))
            // TODO these are more complex for colors, look into later, using primary as placeholder
            is PairArray -> arr.getDataSets(label, label, _colors.primary, _colors.primary)
            is FloatArray -> {
                val dataSet = arr.toDataSet(label, getNextColor(ignoreColor))
                if (overlay is ParabolicSAR)
                    dataSet.lineType = LineType.DOTTED

                listOf(dataSet)
            }
            else -> throw NotImplementedError()
        }
    }

    fun serialize(): String {
        // Format for serialization
        // type:price;logScale:false;overlays:[SMA(234),TEST(2,3,4];
        // Parameters split by ;
        // KeyValue split by   :
        // List split by       ,

        var result = "type:" + when(this) {
            is PriceChart -> "price"
            is VolumeChart -> "volume"
            is IndicatorChart -> "indicator"
            else -> throw NotImplementedError()
        }

        val params = getSerializedParams()
        params.forEach {
            result += ";${it.key}:${it.value}"
        }

        if (overlayCount > 0) {
            result += ";overlays:[" + _overlays.map { it.serialize() }.joinToString(",") + "]"
        }

        return result
    }

    companion object {
        fun deserialize(str: String, colors: ChartColors = ChartColors()): StockChart {
            val params = str.split(";")
            val map = mutableMapOf<String, String>()
            for(param in params) {
                val keyval = param.split(":")
                if (keyval.size != 2)
                    continue

                if (keyval[0] == "overlays")
                    map[keyval[0]] = keyval[1].replace("[", "").replace("]", "").replace("),", ")\t")
                else
                    map[keyval[0]] = keyval[1]
            }

            val overlays = map["overlays"]?.split("\t")?.map { FunctionBase.deserialize(it) }

            val result = when(map["type"]) {
                "price" -> {
                    PriceChart(colors).apply {
                        overlays?.forEach {
                            addOverlay(it as IOverlay)
                        }
                    }
                }
                "volume" -> {
                    VolumeChart(colors).apply {
                        overlays?.forEach {
                            addOverlay(it as ISimpleOverlay)
                        }
                    }
                }
                "indicator" -> {
                    val indicator = FunctionBase.deserialize(map["indicator"]!!) as IIndicator
                    IndicatorChart(indicator, colors).apply {
                        overlays?.forEach {
                            addOverlay(it as ISimpleOverlay)
                        }
                    }
                }

                else -> throw NotImplementedError()
            }

            // Set misc parameters specific to each chart
            result.setSerializedParams(map)

            return result
        }
    }
}
