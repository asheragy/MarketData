package org.cerion.marketdata.core.functions.conditions

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.functions.IFunction
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.overlays.BollingerBands
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.overlays.KAMA
import kotlin.test.*

class ValueIndicatorConditionTest : TestBase() {

    @Test
    fun verifies_insideBandArray() {
        assertFailsWith<IllegalArgumentException> {
            ValueIndicatorCondition(0f, Condition.INSIDE, ExpMovingAverage())
        }
    }

    @Test
    fun toStringTest() {
        assertEquals("50.1 below KAMA 10,2,30", ValueIndicatorCondition(50.1f, Condition.BELOW, KAMA()).toString())
        assertEquals("50.1 inside BB 20,2.1", ValueIndicatorCondition(50.1f, Condition.INSIDE, BollingerBands(20,2.1)).toString())
    }

    @Test
    fun bandArray() {
        runValue(2100f, Condition.ABOVE, BollingerBands(20, 2.0))
        runValue(2050f, Condition.INSIDE, BollingerBands(20, 2.0))
        runValue(2000f, Condition.BELOW, BollingerBands(20, 2.0))
    }

    @Test
    fun floatArray_value() {
        runValue(43f, Condition.ABOVE, RSI(5))
        runValue(42f, Condition.BELOW, RSI(5))
    }

    private fun runValue(value: Float, expectedCondition: Condition, function: IFunction) = runPriceTest {
        for (c in Condition.values()) {
            if (c == Condition.INSIDE && function.resultType != BandArray::class)
                continue

            if (c == expectedCondition)
                assertTrue(ValueIndicatorCondition(value, c, function).eval(it))
            else
                assertFalse(ValueIndicatorCondition(value, c, function).eval(it))
        }
    }
}