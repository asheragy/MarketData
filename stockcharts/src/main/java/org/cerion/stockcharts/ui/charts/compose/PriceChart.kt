package org.cerion.stockcharts.ui.charts.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils
import org.cerion.stockcharts.ui.charts.views.ChartUtils.CHART_HEIGHT_PRICE
import org.cerion.stockcharts.ui.charts.views.ChartUtils.logScaleYAxis
import org.cerion.marketdata.core.charts.PriceChart as PriceChartModel


@Composable
fun PriceChart(
    chartModel: PriceChartModel,
    table: OHLCVTable
) {
    val context = LocalContext.current
    val textColor = if (context.isDarkTheme())
        context.getColor(R.color.secondaryTextColor)
    else
        context.getColor(R.color.primaryTextColor)

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            val chart = CombinedChart(ctx)

            ChartUtils.setChartDefaults(chart, textColor)
            chart.drawOrder = arrayOf(CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE)
            chart.minimumHeight = CHART_HEIGHT_PRICE

            chart
        },

        update = { chart ->
            ChartUtils.setDateAxisLabels(chart, chartModel, table)
            if (chartModel.logScale)
                chart.axisRight.valueFormatter = logScaleYAxis

            val sets = chartModel.getDataSets(table)
            val data = CombinedData()
            if (chartModel.candleData && chartModel.canShowCandleData(table)) {
                data.setData(ChartUtils.getCandleData(sets, context))
            }

            data.setData(ChartUtils.getLineData(sets))
            chart.data = data

            ChartUtils.setLegend(chart, sets, textColor)

            // ensure redraw when inputs change
            chart.invalidate()
        }
    )
}
