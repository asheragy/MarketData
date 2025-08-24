package org.cerion.stockcharts.ui.charts.compose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import org.cerion.stockcharts.R
import org.cerion.stockcharts.common.isDarkTheme
import org.cerion.stockcharts.ui.charts.ChartColorScheme
import org.cerion.stockcharts.ui.charts.views.ChartUtils
import org.cerion.stockcharts.ui.charts.views.ChartUtils.logScaleYAxis
import java.time.LocalDate
import org.cerion.marketdata.core.charts.VolumeChart as VolumeChartModel


@Composable
fun VolumeChart(
    vchart: VolumeChartModel,
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
            CombinedChart(ctx).apply {
                ChartUtils.setChartDefaults(this, textColor)
            }
        },
        update = { chart ->
            ChartUtils.setDateAxisLabels(chart, vchart, table)

            val dataSets = vchart.getDataSets(table)
            chart.data = CombinedData().apply {
                setData(ChartUtils.getBarData(dataSets))
                setData(ChartUtils.getLineData(dataSets))
            }

            ChartUtils.setLegend(chart, dataSets, textColor)
            if (vchart.logScale)
                chart.axisRight.valueFormatter = logScaleYAxis

            // ensure redraw when inputs change
            chart.invalidate()
        }
    )
}

val data = listOf(
    OHLCVRow(
        date = LocalDate.parse("2023-01-01"),
        open = 105.56f,
        high = 115.54f,
        low = 90.81f,
        close = 113.83f,
        volume = 2049433f
    ),
    OHLCVRow(
        date = LocalDate.parse("2023-01-31"),
        open = 117.05f,
        high = 124.53f,
        low = 116.99f,
        close = 122.84f,
        volume = 6355767f
    ),
    OHLCVRow(
        date = LocalDate.parse("2023-03-02"),
        open = 107.97f,
        high = 121.65f,
        low = 103.41f,
        close = 108.25f,
        volume = 8677189f
    ),
    OHLCVRow(
        date = LocalDate.parse("2023-04-01"),
        open = 116.61f,
        high = 129.51f,
        low = 107.06f,
        close = 113.99f,
        volume = 6446210f
    ),
    OHLCVRow(
        date = LocalDate.parse("2023-05-01"),
        open = 121.15f,
        high = 127.30f,
        low = 107.57f,
        close = 123.04f,
        volume = 5040588f
    )
)

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun VolumeChartPreview() {
    val vchart = VolumeChartModel(ChartColorScheme(LocalContext.current))
    vchart.addOverlay(SimpleMovingAverage(20))
    val table = OHLCVTable("FOO", data)

    VolumeChart(vchart, table)
}