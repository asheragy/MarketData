package org.cerion.stockcharts.ui.charts.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils
import org.cerion.marketdata.core.charts.IndicatorChart as IndicatorChartModel


@Composable
fun IndicatorChart(
    chartModel: IndicatorChartModel,
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
            val chart = com.github.mikephil.charting.charts.LineChart(ctx)
            ChartUtils.setChartDefaults(chart, textColor)

            chart
        },

        update = { chart ->
            val sets = chartModel.getDataSets(table)
            chart.data = ChartUtils.getLineData(sets)
            ChartUtils.setLegend(chart, sets, textColor)

            // ensure redraw when inputs change
            chart.invalidate()
        }
    )
}
