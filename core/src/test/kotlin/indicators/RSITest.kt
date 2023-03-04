package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class RSITest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val rsi = RSI(14).eval(it)

        assertEquals(50.0, rsi.first, "p0")
        assertEquals(33.33, rsi[1], "p1")
        assertEquals(34.39, rsi[2], "p2")
        assertEquals(47.57, rsi.last, "last")
    }

}