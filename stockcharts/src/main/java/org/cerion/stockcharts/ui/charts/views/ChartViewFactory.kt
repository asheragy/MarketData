package org.cerion.stockcharts.ui.charts.views

import android.content.Context
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.CombinedData
import org.cerion.marketdata.core.charts.IDataSet
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils.CHART_HEIGHT
import org.cerion.stockcharts.ui.charts.views.ChartUtils.CHART_HEIGHT_PRICE
import org.cerion.stockcharts.ui.charts.views.ChartUtils.blankDescription
import org.cerion.stockcharts.ui.charts.views.ChartUtils.logScaleYAxis

class ChartViewFactory(private val context: Context) {
    private val _textColor =
            if (context.isDarkTheme())
                context.getColor(R.color.secondaryTextColor)
            else
                context.getColor(R.color.primaryTextColor)

    fun getChart(chart: StockChart, table: OHLCVTable): Chart<*> {
        return when(chart) {
            is PriceChart -> getPriceChart(chart, table)
            is IndicatorChart -> getLineChart(chart, table)
            is VolumeChart -> getVolumeChart(chart, table)
            else -> throw NotImplementedError()
        }
    }

    fun getEmptyChart(): Chart<*> {
        return LineChart(context).apply {
            description = blankDescription
            minimumHeight = CHART_HEIGHT
        }
    }

    private fun getPriceChart(pchart: PriceChart, table: OHLCVTable): Chart<*> {
        val chart: BarLineChartBase<*>
        val sets = pchart.getDataSets(table)
        if (pchart.candleData && pchart.canShowCandleData(table)) {
            chart = CombinedChart(context)
            setChartDefaults(chart, pchart, table)
            chart.drawOrder = arrayOf(DrawOrder.CANDLE, DrawOrder.LINE)

            val data = CombinedData()
            // Overloaded so this sets 2 different variables
            data.setData(ChartUtils.getCandleData(sets, context))
            data.setData(ChartUtils.getLineData(sets))
            chart.data = data
        }
        else {
            chart = LineChart(context)
            setChartDefaults(chart, pchart, table)

            val lineData = ChartUtils.getLineData(sets)
            chart.data = lineData
        }

        chart.setDrawMarkers(false)
        chart.minimumHeight = CHART_HEIGHT_PRICE
        if (pchart.logScale)
            chart.axisRight.valueFormatter = logScaleYAxis

        setLegend(chart, sets)
        return chart
    }

    private fun getLineChart(ichart: IndicatorChart, table: OHLCVTable): Chart<*> {
        return LineChart(context).apply {
            setChartDefaults(this, ichart, table)
            minimumHeight = CHART_HEIGHT

            val sets = ichart.getDataSets(table)
            data = ChartUtils.getLineData(sets)
            setLegend(this, sets)
        }
    }

    private fun getVolumeChart(vchart: VolumeChart, table: OHLCVTable): Chart<*> {
        return CombinedChart(context).apply {
            setChartDefaults(this, vchart, table)

            val dataSets = vchart.getDataSets(table)
            data = CombinedData().apply {
                setData(ChartUtils.getBarData(dataSets))
                setData(ChartUtils.getLineData(dataSets))
            }

            setLegend(this, dataSets)
            if (vchart.logScale)
                axisRight.valueFormatter = logScaleYAxis
        }
    }

    private fun setChartDefaults(chart: BarLineChartBase<*>, stockchart: StockChart, table: OHLCVTable) {
        ChartUtils.setChartDefaults(chart, _textColor)
        ChartUtils.setDateAxisLabels(chart, stockchart, table)
    }

    private fun setLegend(chart: Chart<*>, sets: List<IDataSet>) {
        ChartUtils.setLegend(chart, sets, _textColor)
    }
}