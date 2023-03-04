package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.indicators.Vortex
import org.cerion.marketdata.core.overlays.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StockChartTest : TestBase() {

    @Test
    fun dates_firstElementRemoved() = runPriceTest {
        val chart = VolumeChart()
        val dates = chart.getDates(it)

        assertEquals(it.size - 1, dates.size)
        assertEquals(it.dates[1], dates[0])
        assertEquals(it.dates[it.size - 1], dates[dates.size - 1])
    }

    @Test
    fun dataSets_firstElementRemoved() = runPriceTest {
        val chart = PriceChart()
        chart.addOverlay(SimpleMovingAverage())
        chart.addOverlay(BollingerBands())
        chart.addOverlay(PriceChannels())
        var dataSets = chart.getDataSets(it)

        val count = it.size - 1
        for(set in dataSets)
            assertEquals(count, set.size)

        val vchart = VolumeChart()
        vchart.addOverlay(ExpMovingAverage())
        dataSets = chart.getDataSets(it)
        for(set in dataSets)
            assertEquals(count, set.size)

        val ichart = IndicatorChart(Vortex())
        dataSets = ichart.getDataSets(it)
        for(set in dataSets)
            assertEquals(count, set.size)
    }

    @Test
    fun stockChart_Minimalserialize() {
        // Extra fields are not added to string if default values (Except function parameters)
        var chart: StockChart = PriceChart()
        chart.addOverlay(SimpleMovingAverage(49))
        assertEquals("type:price;overlays:[SMA(49)]", chart.serialize())

        chart = VolumeChart()
        assertEquals("type:volume", chart.serialize())
    }

    @Test
    fun stockChart_PriceChart_deserialize() {
        val chart = PriceChart()
        var deserializedChart = StockChart.deserialize(chart.serialize()) as PriceChart
        assertFalse(deserializedChart.candleData)
        assertTrue(deserializedChart.showPrice)
        assertFalse(deserializedChart.logScale)
        assertEquals(0, deserializedChart.overlayCount)

        // With overlays + non-Defaults
        chart.addOverlay(BollingerBands(21,3.0))
        chart.addOverlay(IchimokuClouds())
        chart.candleData = true
        chart.showPrice = false
        chart.logScale = true
        deserializedChart = StockChart.deserialize(chart.serialize()) as PriceChart
        assertEquals(2, deserializedChart.overlayCount)
        assertTrue(deserializedChart.candleData)
        assertFalse(deserializedChart.showPrice)
        assertTrue(deserializedChart.logScale)

        val ol1 = deserializedChart.getOverlay(0)
        assertTrue(ol1 is BollingerBands)
        assertEquals(21, ol1.params[0])
        assertEquals(3.0f, ol1.params[1])

        assertTrue(deserializedChart.getOverlay(1) is IchimokuClouds)
    }

    @Test
    fun stockChart_VolumeChart_deserialize() {
        val chart = VolumeChart()
        var deserializedChart = StockChart.deserialize(chart.serialize())
        assertTrue(deserializedChart is VolumeChart)
        assertFalse(deserializedChart.logScale)

        // With overlays + log scale
        chart.addOverlay(BollingerBands(21,3.0))
        chart.addOverlay(SimpleMovingAverage(25))
        chart.logScale = true
        deserializedChart = StockChart.deserialize(chart.serialize())
        assertTrue(deserializedChart is VolumeChart)
        assertEquals(2, deserializedChart.overlayCount)
        assertTrue(deserializedChart.logScale)

        val ol1 = deserializedChart.getOverlay(0)
        assertTrue(ol1 is BollingerBands)
        assertEquals(21, ol1.params[0])
        assertEquals(3.0f, ol1.params[1])

        val ol2 = deserializedChart.getOverlay(1)
        assertTrue(ol2 is SimpleMovingAverage)
        assertEquals(25, ol2.params[0])
    }

    @Test
    fun stockChart_IndicatorChart_deserialize() {
        val chart = IndicatorChart(RSI(11))
        var deserializedChart = StockChart.deserialize(chart.serialize()) as IndicatorChart
        assertEquals(Indicator.RSI, deserializedChart.id)
        assertEquals(11, deserializedChart.indicator.params[0])
        assertEquals(0, deserializedChart.overlayCount)

        // With overlays
        chart.addOverlay(BollingerBands(21,3.0))
        chart.addOverlay(SimpleMovingAverage(25))
        deserializedChart = StockChart.deserialize(chart.serialize()) as IndicatorChart
        assertEquals(Indicator.RSI, deserializedChart.id)
        assertEquals(11, deserializedChart.indicator.params[0])
        assertEquals(2, deserializedChart.overlayCount)

        val ol1 = deserializedChart.getOverlay(0)
        assertTrue(ol1 is BollingerBands)
        assertEquals(21, ol1.params[0])
        assertEquals(3.0f, ol1.params[1])

        val ol2 = deserializedChart.getOverlay(1)
        assertTrue(ol2 is SimpleMovingAverage)
        assertEquals(25, ol2.params[0])
    }
}