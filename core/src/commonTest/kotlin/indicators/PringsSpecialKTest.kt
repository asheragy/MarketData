package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class PringsSpecialKTest : TestBase() {

    @Test
    fun pringsSpecialK_test() = runPriceTest {
        val arr = PringsSpecialK().eval(it)

        // TODO verify values online, just doing these pre-refactor
        assertEquals(0.0, arr.first, "first")
        assertEquals(-57.52, arr[1], "position 1")
        assertEquals(-74.84, arr[2], "position 2")
        assertEquals(112.63, arr.last, "last")
    }
}