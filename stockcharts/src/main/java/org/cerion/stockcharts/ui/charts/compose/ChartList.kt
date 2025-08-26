package org.cerion.stockcharts.ui.charts.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.model.OHLCVTable

val emptyTable = OHLCVTable("", emptyList())

@Composable
fun ChartList(
    charts: List<ChartModel>,
    table: OHLCVTable?,
    onClick: (StockChart) -> Unit = {},
) {
    var viewPort by remember { mutableStateOf<ViewportPayload?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(charts) { chartModel ->
            StockChart(
                model = chartModel,
                table = table ?: emptyTable,
                onViewPortChange = {
                    viewPort = ViewportPayload(it)
                },
                onClick = onClick,
                viewPort = viewPort
            )
        }
    }
}