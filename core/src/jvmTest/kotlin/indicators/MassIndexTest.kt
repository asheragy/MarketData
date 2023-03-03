package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class MassIndexTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val ma = MassIndex(23).eval(it)
        assertEquals(23.00, ma[0], "massIndex 0")
        assertEquals(23.83, ma[1], "massIndex 1")
        assertEquals(20.93, ma[10], "massIndex p-2")
        assertEquals(21.85, ma[22], "massIndex p-1")
        assertEquals(21.75, ma[23], "massIndex p")
        assertEquals(23.76, ma.last, "massIndex last")
    }
}