package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.functions.IPriceOverlay
import org.cerion.marketdata.core.overlays.BollingerBands
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.*


class PriceConditionTest : TestBase() {

    @Test
    fun verifiesCondition() {
        assertFailsWith<IllegalArgumentException> {
            PriceCondition(Condition.INSIDE, SimpleMovingAverage())
        }
    }

    @Test
    fun floatOverlay() {
        testCondition(Condition.BELOW, SimpleMovingAverage(20))
        testCondition(Condition.ABOVE, SimpleMovingAverage(94))
    }

    @Test
    fun bandOverlay() {
        testCondition(Condition.INSIDE, BollingerBands(20, 2.0))
        testCondition(Condition.ABOVE, BollingerBands(94, 0.2))
        testCondition(Condition.BELOW, BollingerBands(30, 0.2))
    }

    @Test
    fun toStringTest() {
        assertEquals("Price above SMA 29", PriceCondition(Condition.ABOVE, SimpleMovingAverage(29)).toString())
        assertEquals("Price below SMA 29", PriceCondition(Condition.BELOW, SimpleMovingAverage(29)).toString())
        assertEquals("Price inside BB 30,3.1", PriceCondition(Condition.INSIDE, BollingerBands(30, 3.1)).toString())
    }

    private fun testCondition(trueCondition: Condition, overlay: IPriceOverlay) = runPriceTest {
        for (c in Condition.values()) {
            if (c == Condition.INSIDE && overlay.resultType != BandArray::class)
                continue

            val condition = PriceCondition(c, overlay)
            if (c == trueCondition)
                assertTrue(condition.eval(it), "Price $trueCondition with $overlay")
            else
                assertFalse(condition.eval(it), "Price NOT $trueCondition with $overlay")
        }
    }
}