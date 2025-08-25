package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix
import androidx.compose.runtime.Composable
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.model.OHLCVTable

@Composable
fun StockChart(
    model: ChartModel<*>,
    table: OHLCVTable,
    onViewPortChange: (Matrix) -> Unit = {},
    onClick: (StockChart) -> Unit = {},
    viewPort: ViewportPayload? = null
) {
    when(model.value) {
        is PriceChart -> {
            PriceChart(
                model = model as ChartModel<PriceChart>,
                table = table,
                onViewPortChange = onViewPortChange,
                onClick = onClick,
                viewPort = viewPort)
        }
        is VolumeChart -> VolumeChart(model as ChartModel<VolumeChart>, table, onViewPortChange, onClick, viewPort)
        is IndicatorChart -> IndicatorChart(model as ChartModel<IndicatorChart>, table, onViewPortChange, onClick, viewPort)
    }
}