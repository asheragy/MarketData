package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class TrueStrengthIndexTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val arr = TrueStrengthIndex(20, 10).eval(it)
        assertEquals(0.0, arr.first, "tsi 0")
        assertEquals(0.0, arr[1], "tsi 1")
        assertEquals(-94.56, arr[2], "tsi 2")
        assertEquals(-26.05, arr[18], "tsi p-2")
        assertEquals(-24.36, arr[19], "tsi p-1")
        assertEquals(-20.65, arr[20], "tsi p")
        assertEquals(-36.77, arr[200], "tsi 200")
        assertEquals(-0.92, arr.last, "tsi last")
    }
}