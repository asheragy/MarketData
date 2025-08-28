package org.cerion.stockcharts.ui.charts.compose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import org.cerion.stockcharts.ui.AppTheme
import org.cerion.stockcharts.ui.charts.ChartColorScheme
import org.cerion.stockcharts.ui.charts.ChartsViewModel

val emptyTable = OHLCVTable("", emptyList())

@Composable
fun ChartList(
    charts: List<ChartModel>,
    table: OHLCVTable?,
    scrollConnection: NestedScrollConnection,
    loading: Boolean,
    interval: Interval,
    ranges: List<String>,
    viewModel: ChartsViewModel? = null,
) {
    val context = LocalContext.current
    var viewPort by remember { mutableStateOf<ViewportPayload?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollConnection),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ranges.take(4).forEach { label ->
                        AssistChip(
                            onClick = {
                                val index = ranges.indexOf(label)
                                if (index >= 0) {
                                    Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show()
                                    viewModel?.setRange(index)
                                }
                            },
                            label = { Text(label) }
                        )
                    }
                }

                IntervalDropDownMenu(interval) {
                    viewModel?.setInterval(it)
                }
            }
        }
        items(charts) { chartModel ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                if (loading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                StockChart(
                    model = chartModel,
                    table = table ?: emptyTable,
                    onViewPortChange = {
                        viewPort = ViewportPayload(it)
                    },
                    onClick = {
                        viewModel?.editChart(it)
                    },
                    viewPort = viewPort
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalDropDownMenu(interval: Interval, onSelect: (Interval) -> Unit) {
    val items = mapOf(
        "Daily" to Interval.DAILY,
        "Weekly" to Interval.WEEKLY,
        "Monthly" to Interval.MONTHLY,
        "Quarterly" to Interval.QUARTERLY
    ).toList()

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(interval) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = items.first { it.second == selectedItem }.first,
            onValueChange = {},
            readOnly = true,
            //label = { Text("Select an option") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .width(130.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.first) },
                    onClick = {
                        onSelect(item.second)
                        selectedItem = item.second
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ChartListPreview() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val chart = PriceChart(ChartColorScheme(LocalContext.current))
    chart.addOverlay(SimpleMovingAverage(20))
    val ranges = listOf("1M", "6M", "1Y", "MAX")

    AppTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ChartList(
                listOf(ChartModel(chart)),
                table,
                scrollBehavior.nestedScrollConnection,
                false,
                Interval.DAILY,
                ranges
            )
        }
    }

}