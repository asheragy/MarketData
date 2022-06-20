package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.indicators.AccumulationDistributionLine
import org.cerion.marketdata.core.indicators.MACD
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.indicators.Vortex
import org.cerion.marketdata.core.overlays.BollingerBands
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartColorsTest : TestBase() {

    private val colors = ChartColors().apply {
        primary = 1
        primaryBlue = 2
        volumneBlue = 3
        positiveGreen = 4
        negativeRed = 5
        primaryPurple = 6

        orange = 10
        teal = 11
        yellow = 12
        secondaryBlue = 13
        secondaryRed = 14
        secondaryGreen = 15
    }

    @Test
    fun chartColors_basicCharts() = runPriceTest {
        // Price chart is primary blue
        val priceChart = PriceChart(colors)
        var data = priceChart.getDataSets(it)
        assertEquals(2, data[0].color)

        // Misc line chart is primary
        val indicatorChart = IndicatorChart(AccumulationDistributionLine(), colors)
        data = indicatorChart.getDataSets(it)
        assertEquals(1, data[0].color)

        // Volume chart
        val volumeChart = VolumeChart(colors)
        data = volumeChart.getDataSets(it)
        assertEquals(3, data[0].color)

        // Pair
        val pairChart = IndicatorChart(Vortex(), colors)
        data = pairChart.getDataSets(it)
        assertEquals(4, data[0].color)
        assertEquals(5, data[1].color)
    }

    @Test
    fun chartColors_MACD() = runPriceTest {
        val chart = IndicatorChart(MACD(), colors)
        val data = chart.getDataSets(it)
        assertEquals(6, data[0].color)
        assertEquals(10, data[1].color)
        assertEquals(13, data[2].color)
    }

    @Test
    fun chartColors_overlaysRotateColor() = runPriceTest {
        val chart = IndicatorChart(AccumulationDistributionLine(), colors)
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(ExpMovingAverage())
        chart.addOverlay(BollingerBands())
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(SimpleMovingAverage())

        val data = chart.getDataSets(it)
        assertEquals(colors.primary, data[0].color)
        assertEquals(colors.getOverlayColor(0), data[1].color)
        assertEquals(colors.getOverlayColor(1), data[2].color)
        assertEquals(colors.getOverlayColor(2), data[3].color)
        assertEquals(colors.getOverlayColor(2), data[4].color) // Bands are 2 sets with the same color

        assertEquals(colors.getOverlayColor(3), data[5].color)
        assertEquals(colors.getOverlayColor(4), data[6].color)
        assertEquals(colors.getOverlayColor(5), data[7].color)
        assertEquals(colors.getOverlayColor(0), data[8].color) // Starts back at zero
    }

    @Test
    fun chartColors_specialCaseColors() = runPriceTest {
        val chart = IndicatorChart(RSI(), colors)
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(ExpMovingAverage())

        // Primary color is purple
        val data = chart.getDataSets(it)
        assertEquals(colors.primaryPurple, data[0].color)
        assertEquals(colors.getOverlayColor(0), data[1].color)
        assertEquals(colors.getOverlayColor(1), data[2].color)
    }
}