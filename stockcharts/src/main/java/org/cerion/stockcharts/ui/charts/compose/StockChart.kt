package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable

@Composable
fun StockChart(
    model: ChartModel,
    table: OHLCVTable,
    onViewPortChange: (Matrix) -> Unit = {},
    onClick: (StockChart) -> Unit = {},
    viewPort: ViewportPayload? = null
) {
    var lastModelVersion by remember { mutableLongStateOf(0L) }
    var lastInterval by remember { mutableStateOf<Interval?>(null) }
    var lastSize by remember { mutableIntStateOf(0) }

    val updateData = (lastModelVersion != model.version) || (lastInterval != table.interval) || (lastSize != table.size)
    if (lastModelVersion != model.version)
        lastModelVersion = model.version
    if (lastInterval != table.interval)
        lastInterval = table.interval
    if (lastSize != table.size)
        lastSize = table.size

    when(model.value) {
        is PriceChart -> {
            PriceChart(
                model = model.value,
                table = table,
                updateData,
                onViewPortChange = onViewPortChange,
                onClick = onClick,
                viewPort = viewPort)
        }
        is VolumeChart -> VolumeChart(model.value, table, updateData, onViewPortChange, onClick, viewPort)
        is IndicatorChart -> IndicatorChart(model.value, table, updateData, onViewPortChange, onClick, viewPort)
    }
}