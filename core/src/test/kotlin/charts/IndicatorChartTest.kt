package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.indicators.MACD
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.Test
import kotlin.test.assertEquals

class IndicatorChartTest {

    @Test
    fun copyTest() {
        val c1 = IndicatorChart(MACD(1, 2, 3))
        c1.addOverlay(SimpleMovingAverage(45))
        val c2 = c1.copy() as IndicatorChart

        assertEquals(c1.id, c2.id, "indicator type")

        val i1 = c1.indicator
        val i2 = c2.indicator
        assertEquals(i1.params, i2.params, "parameters")

        // Change original and verify copy does not change
        c1.indicator.setParams(99, 2, 3)
        c1.getOverlay(0).setParams(39)
        c1.addOverlay(ExpMovingAverage(55))

        // Verify current
        assertEquals(99, i1.params[0], "original parameters")
        assertEquals(39, c1.getOverlay(0).params[0], "original overlay parameter")
        assertEquals(2, c1.overlayCount, "original 2nd overlay")

        // Verify copy
        assertEquals(1, i2.params[0], "copied parameters")
        assertEquals(45, c2.getOverlay(0).params[0], "copied overlay parameter")
        assertEquals(1, c2.overlayCount, "overlay count")
    }
}