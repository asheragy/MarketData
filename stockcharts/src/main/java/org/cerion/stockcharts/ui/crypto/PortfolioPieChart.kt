package org.cerion.stockcharts.ui.crypto

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

class PortfolioPieChart(context: Context?, attrs: AttributeSet?) : PieChart(context, attrs) {

    init {
        legend.isEnabled = false
        description = null
        setEntryLabelColor(Color.BLACK)
    }

    fun setPositions(positions: List<Position>) {
        val total = positions.sumByDouble { it.totalValue }.toFloat() / 100

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        val cashPosition = positions.firstOrNull { it.cash }
        val green = ColorTemplate.PASTEL_COLORS[1]
        if (cashPosition != null) {
            colors.add(green)
            entries.add(PieEntry(cashPosition.totalValue.toFloat() / total, "Cash"))
        }

        for (p in positions.filter { !it.cash }) {
            val value = (p.totalValue / total).toFloat()
            val label = if(p.cash) "Cash" else p.symbol
            entries.add(PieEntry(value, label))
        }

        // Data set
        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 2f
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = PercentFormatter()

        val colorsWithoutGreen = ColorTemplate.PASTEL_COLORS.filter { it != green }
        colors.addAll(colorsWithoutGreen)
        colors.addAll(colorsWithoutGreen)
        dataSet.colors = colors
        val pieData = PieData(dataSet)
        data = pieData
        invalidate()
    }
}