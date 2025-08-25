package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.DefaultChartGestureListener
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils
import org.cerion.marketdata.core.charts.IndicatorChart as IndicatorChartModel


@Composable
fun IndicatorChart(
    chartModel: ChartModel<IndicatorChartModel>,
    table: OHLCVTable,
    onViewPortChange: (Matrix) -> Unit = {},
    onClick: (IndicatorChartModel) -> Unit = {},
    viewPort: ViewportPayload? = null
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
            val sets = chartModel.value.getDataSets(table)
            chart.data = ChartUtils.getLineData(sets)
            ChartUtils.setLegend(chart, sets, textColor)

            if (viewPort != null && viewPort.matrix != chart.viewPortHandler.matrixTouch) {
                val matrix = Matrix(viewPort.matrix)
                chart.viewPortHandler.refresh(matrix, chart, true)
            }

            val matrix = chart.viewPortHandler.matrixTouch
            chart.onChartGestureListener = object : DefaultChartGestureListener() {
                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                    super.onChartScale(me, scaleX, scaleY)
                    onViewPortChange(matrix)
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                    super.onChartTranslate(me, dX, dY)
                    onViewPortChange(matrix)
                }

                override fun onChartSingleTapped(me: MotionEvent?) {
                    super.onChartSingleTapped(me)
                    onClick(chartModel.value)
                }
            }

            // ensure redraw when inputs change
            chart.invalidate()
        }
    )
}
