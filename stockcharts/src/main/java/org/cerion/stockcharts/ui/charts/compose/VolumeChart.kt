package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.DefaultChartGestureListener
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils
import org.cerion.stockcharts.ui.charts.views.ChartUtils.logScaleYAxis
import org.cerion.marketdata.core.charts.VolumeChart as VolumeChartModel


@Composable
fun VolumeChart(
    model: VolumeChartModel,
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
            println("Creating VolumeChart")
            CombinedChart(ctx).apply {
                ChartUtils.setChartDefaults(this, textColor)
                this.setDrawMarkers(false)
            }
        },
        update = { chart ->
            if (updateData && table.size > 0) {
                ChartUtils.setDateAxisLabels(chart, model, table)

                val dataSets = model.getDataSets(table)
                chart.data = CombinedData().apply {
                    setData(ChartUtils.getBarData(dataSets))
                    setData(ChartUtils.getLineData(dataSets))
                }

                ChartUtils.setLegend(chart, dataSets, textColor)
                chart.axisRight.valueFormatter = if (model.logScale) logScaleYAxis else null

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
