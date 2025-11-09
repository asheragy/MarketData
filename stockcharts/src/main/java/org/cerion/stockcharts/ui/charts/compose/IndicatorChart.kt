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
    model: IndicatorChartModel,
    table: OHLCVTable,
    updateData: Boolean = true,
    onViewPortChange: (Matrix) -> Unit = {},
    onClick: () -> Unit = {},
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
            if (updateData && table.size > 0) {
                val sets = model.getDataSets(table)
                chart.data = ChartUtils.getLineData(sets)
                ChartUtils.setLegend(chart, sets, textColor)

                chart.onChartGestureListener = object : DefaultChartGestureListener() {
                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                        super.onChartScale(me, scaleX, scaleY)
                        onViewPortChange(chart.viewPortHandler.matrixTouch)
                    }

                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                        super.onChartTranslate(me, dX, dY)
                        onViewPortChange(chart.viewPortHandler.matrixTouch)
                    }

                    override fun onChartSingleTapped(me: MotionEvent?) {
                        super.onChartSingleTapped(me)
                        onClick()
                    }
                }
            }

            if (viewPort != null && viewPort.matrix != chart.viewPortHandler.matrixTouch) {
                val matrix = Matrix(viewPort.matrix)
                chart.viewPortHandler.refresh(matrix, chart, true)
            }

            // ensure redraw when inputs change
            chart.invalidate()
        }
    )
}
