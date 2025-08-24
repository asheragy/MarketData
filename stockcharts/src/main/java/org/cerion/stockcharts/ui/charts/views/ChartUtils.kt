package org.cerion.stockcharts.ui.charts.views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.cerion.marketdata.core.charts.CandleDataSet
import org.cerion.marketdata.core.charts.DataSet
import org.cerion.marketdata.core.charts.IDataSet
import org.cerion.marketdata.core.charts.LineType
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.stockcharts.R
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.exp

object ChartUtils {
    private var dateFormat = DateTimeFormatter.ofPattern("MMM d, yy")
    private var dateFormatMonthly = DateTimeFormatter.ofPattern("MMM ''yy")
    const val CHART_HEIGHT_PRICE = 800
    const val CHART_HEIGHT = 400
    val blankDescription = Description().apply { text = "" }

    // Round to 2 significant figures
    val logScaleYAxis: IAxisValueFormatter = object : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
            // Round to 2 significant figures
            val actual = exp(value.toDouble())
            var bd = BigDecimal(actual)
            bd = bd.round(MathContext(2))
            return bd.toPlainString()
        }
    }

    fun getBarData(sets: List<IDataSet>): BarData {
        val result = mutableListOf<IBarDataSet>()

        sets.filter { it.lineType == LineType.BAR }
            .forEach {
                val set = it as DataSet

                val entries = set.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value)
                }

                val dataSet = BarDataSet(entries, set.label)
                dataSet.setDrawValues(false)
                dataSet.color = set.color
                result.add(dataSet)
            }

        return BarData(result)
    }

    fun getLineData(sets: List<IDataSet>): LineData {
        val result = mutableListOf<ILineDataSet>()

        for (curr in sets) {
            if (curr.lineType === LineType.LINE || curr.lineType === LineType.DOTTED) {
                val set = curr as DataSet
                val entries = set.mapIndexed { index, value ->
                    // TODO previous code checked for NaN, could be other issues if that comes up...
                    Entry(index.toFloat(), value)
                }

                val lineDataSet = LineDataSet(entries, set.label).apply {
                    setDrawCircles(false)
                    setDrawValues(false)
                    color = set.color

                    if (set.lineType === LineType.DOTTED) {
                        // https://github.com/PhilJay/MPAndroidChart/pull/2622 This should be Transparent but using White because of bug
                        color = Color.rgb(250, 250, 250)
                        setDrawCircles(true)
                        circleRadius = 1f
                        setCircleColor(set.color)
                    }
                }

                result.add(lineDataSet)
            }
        }

        return LineData(result)
    }

    fun getCandleData(sets: List<IDataSet>, context: Context): CandleData {
        for (set in sets) {
            if (set.lineType === LineType.CANDLE) {
                val entries = mutableListOf<CandleEntry>()
                val cds = set as CandleDataSet

                for (i in 0 until set.size)
                    entries.add(CandleEntry(i.toFloat(), cds.getHigh(i), cds.getLow(i), cds.getOpen(i), cds.getClose(i))) // order is high, low, open, close

                val dataSet = com.github.mikephil.charting.data.CandleDataSet(entries, set.label)
                dataSet.setDrawValues(false)
                dataSet.decreasingColor = context.getColor(R.color.negative_red)
                dataSet.decreasingPaintStyle = Paint.Style.FILL
                dataSet.increasingColor = context.getColor(R.color.positive_green)
                dataSet.increasingPaintStyle = Paint.Style.FILL

                return CandleData(dataSet)
            }
        }

        return CandleData()
    }

    fun setChartDefaults(chart: BarLineChartBase<*>, textColor: Int) {
        if (chart.data != null)
            throw AssertionError("chart defaults should be set before data") // Needed for viewPortOffsets to work properly

        chart.apply {
            description = blankDescription
            minimumHeight = CHART_HEIGHT

            //Set Y axis
            axisLeft.setDrawLabels(false)
            axisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            axisRight.setLabelCount(3, false)
            axisRight.textColor = textColor

            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            // Always start at position 0 even if data set starts after that
            xAxis.axisMinimum = 0f
            xAxis.textColor = textColor

            setViewPortOffsets(0f, viewPortHandler.offsetTop(), 0f, viewPortHandler.offsetBottom())
        }
    }

    fun setDateAxisLabels(chart: BarLineChartBase<*>, stockchart: StockChart, table: OHLCVTable) {
        chart.xAxis.valueFormatter = getAxisFormatter(stockchart.getDates(table), table.interval)
    }

    fun setLegend(chart: Chart<*>, sets: List<IDataSet>, textColor: Int) {
        val entries = mutableListOf<LegendEntry>()
        var lastLabel = ""
        var lastColor = -1

        for (set in sets) {
            val label = set.label
            val color = set.color
            var entry: LegendEntry? = null
            if (lastLabel.contentEquals(label)) {
                if (lastColor != color) {
                    entry = LegendEntry(label, Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, color)
                    entries[entries.size - 1].label = null // label needs to go on the last one added
                }
            } else {
                entry = LegendEntry(label, Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, color)
            }

            if (entry != null)
                entries.add(entry)

            lastLabel = label
            lastColor = color
        }

        chart.legend.apply {
            setCustom(entries)
            setDrawInside(true)
            orientation = Legend.LegendOrientation.VERTICAL
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            isWordWrapEnabled = false
            this.textColor = textColor
        }
    }


    private fun getAxisFormatter(dates: Array<LocalDate>, interval: Interval): IAxisValueFormatter {
        return object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                val position = value.toInt()
                if (position >= dates.size)
                    return ""

                val date: LocalDate = dates[position]
                return if (interval === Interval.MONTHLY)
                    dateFormatMonthly.format(date)
                else
                    dateFormat.format(date)
            }
        }
    }
}