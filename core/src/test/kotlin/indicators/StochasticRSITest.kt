package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class StochasticRSITest : TestBase() {

    @Test
    fun stochasticRSI_defaults() = runPriceTest {
        val arr = StochasticRSI().eval(it)

        // TODO verify value online, just doing these pre-refactor
        assertEquals(0.57, arr.last, "last")
    }
}