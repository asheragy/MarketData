package org.cerion.stockcharts.ui.crypto

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

data class PieSlice(
    val label: String,
    val value: Float,
    val color: Int? = null
)

val LIGHT_GREEN = Color.rgb(192, 255, 140)
val EXTENDED_VORDIPLOM_COLORS = intArrayOf(
    // Original
    LIGHT_GREEN,
    Color.rgb(255, 247, 140),  // light yellow
    Color.rgb(255, 208, 140),  // peach
    Color.rgb(140, 234, 255),  // sky blue
    Color.rgb(255, 140, 157),  // pink

    // Extended
    Color.rgb(180, 180, 255),  // lavender blue
    Color.rgb(200, 255, 200),  // mint green
    Color.rgb(255, 200, 220),  // rose pink
    Color.rgb(255, 220, 180),  // apricot
    Color.rgb(220, 200, 255) // lilac purple
)

@Composable
fun SimplePieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier
) {
    // Rotate colors
    val definedColors = slices.mapNotNull { it.color }.distinct()
    val availableColors = EXTENDED_VORDIPLOM_COLORS.filter { !definedColors.contains(it) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setEntryLabelColor(Color.BLACK)
            }
        },
        update = { chart ->
            val entries = slices.map { PieEntry(it.value, it.label) }

            val colors = mutableListOf<Int>()
            var i = 0
            slices.forEach {
                if (it.color != null)
                    colors.add(it.color)
                else {
                    colors.add(availableColors[i % availableColors.size])
                    i++
                }
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                this.valueTextSize = 12f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

@Preview
@Composable
fun Demo() {
    val slices = listOf(
        PieSlice("BTC", 40f),
        PieSlice("ETH", 30f),
        PieSlice("SOL", 30f),
        PieSlice("XPR", 30f),
        PieSlice("LTC", 30f),
        PieSlice("DOGE", 30f),
        PieSlice("Cash", 30f, LIGHT_GREEN),
    )
    SimplePieChart(slices, modifier = Modifier.fillMaxSize())
}
