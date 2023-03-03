package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class StochasticTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val stoch = Stochastic(14, 1, 1).eval(it)

        // Verified on stockcharts
        assertEquals(42.53, stoch.first, "position 0")
        assertEquals(2.47, stoch[1], "position 1")
        assertEquals(63.48, stoch[13], "position 13")
        assertEquals(25.02, stoch[14], "position 14")
        assertEquals(57.40, stoch.last, "position last")
    }

}