package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.functions.IFunction
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.overlays.BollingerBands
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.*

class IndicatorConditionTest : TestBase() {

    @Test
    fun verifies_floatArray() {
        assertFailsWith<IllegalArgumentException> {
            IndicatorCondition(BollingerBands(), Condition.ABOVE, ExpMovingAverage())
        }
    }

    @Test
    fun verifies_floatArray2() {
        assertFailsWith<IllegalArgumentException> {
            IndicatorCondition(SimpleMovingAverage(), Condition.ABOVE, BollingerBands())
        }
    }

    @Test
    fun verifies_condition() {
        assertFailsWith<IllegalArgumentException> {
            IndicatorCondition(SimpleMovingAverage(), Condition.INSIDE, ExpMovingAverage())
        }
    }

    @Test
    fun basic() {
        runFunction(SimpleMovingAverage(20), Condition.BELOW, ExpMovingAverage(50))
        runFunction(ExpMovingAverage(50), Condition.ABOVE, SimpleMovingAverage(20))
        runFunction(RSI(7), Condition.BELOW, RSI(14))
    }

    @Test
    fun toStringTest() {
        assertEquals(IndicatorCondition(ExpMovingAverage(20), Condition.ABOVE, ExpMovingAverage(30)).toString(), "EMA 20 above EMA 30")
    }

    private fun runFunction(f1: IFunction, expectedCondition: Condition, f2: IFunction) = runPriceTest {
        for (c in Condition.values()) {
            if (c == Condition.INSIDE)
            // not applicable to this constructor
                continue

            if (c == expectedCondition) {
                assertTrue(IndicatorCondition(f1, c, f2).eval(it))
                assertFalse(IndicatorCondition(f2, c, f1).eval(it))
            } else {
                assertFalse(IndicatorCondition(f1, c, f2).eval(it))
                assertTrue(IndicatorCondition(f2, c, f1).eval(it))
            }
        }
    }
}