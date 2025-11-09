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
import org.cerion.stockcharts.ui.charts.views.ChartUtils.CHART_HEIGHT_PRICE
import org.cerion.stockcharts.ui.charts.views.ChartUtils.logScaleYAxis
import org.cerion.marketdata.core.charts.PriceChart as PriceChartModel


@Composable
fun PriceChart(
    model: PriceChartModel,
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
            val chart = CombinedChart(ctx)

            ChartUtils.setChartDefaults(chart, textColor)
            chart.drawOrder = arrayOf(CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE)
            chart.minimumHeight = CHART_HEIGHT_PRICE
            chart.setDrawMarkers(false) // Divide by zero bug if this isn't set
            chart
        },

        update = { chart ->
            if (updateData && table.size > 0) {
                ChartUtils.setDateAxisLabels(chart, model, table)
                chart.axisRight.valueFormatter = if (model.logScale) logScaleYAxis else null

                val sets = model.getDataSets(table)
                val data = CombinedData()
                if (model.candleData && model.canShowCandleData(table)) {
                    data.setData(ChartUtils.getCandleData(sets, context))
                }

                data.setData(ChartUtils.getLineData(sets))
                chart.data = data

                ChartUtils.setLegend(chart, sets, textColor)


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
                        //listener?.onClick(model.value)
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
