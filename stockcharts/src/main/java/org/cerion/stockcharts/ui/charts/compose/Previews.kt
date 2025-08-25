package org.cerion.stockcharts.ui.charts.compose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.indicators.MACD
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import org.cerion.stockcharts.ui.charts.ChartColorScheme
import java.time.LocalDate


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

private val table = OHLCVTable("FOO", data)

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PriceChartPreview() {
    val chart = PriceChart(ChartColorScheme(LocalContext.current))
    chart.addOverlay(SimpleMovingAverage(20))

    val candleChart = chart.copy() as PriceChart
    candleChart.candleData = true
    Column {
        PriceChart(ChartModel(chart), table)
        PriceChart(ChartModel(candleChart), table)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun VolumeChartPreview() {
    val vchart =
        org.cerion.marketdata.core.charts.VolumeChart(ChartColorScheme(LocalContext.current))
    vchart.addOverlay(SimpleMovingAverage(20))

    VolumeChart(ChartModel(vchart), table)
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun IndicatorChartPreview() {
    val model = IndicatorChart(MACD(), ChartColorScheme(LocalContext.current))
    IndicatorChart(ChartModel(model), table)
}