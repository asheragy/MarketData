package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class ChandelierExitTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        var arr = ChandelierExit(22, 3.0).eval(it)

        assertEquals(1359.08, arr.pos(0), "chandelierExit 0")
        assertEquals(1356.605, arr.pos(1), "chandelierExit 1")
        assertEquals(1376.86, arr.pos(20), "chandelierExit 20")
        assertEquals(1379.12, arr.pos(21), "chandelierExit 21")
        assertEquals(1374.90, arr.pos(22), "chandelierExit 22")
        assertEquals(2007.38, arr.pos(3950), "chandelierExit 3950")
        assertEquals(2030.88, arr.pos(it.size - 1), "chandelierExit last")

        arr = ChandelierExit(15, 2.5).eval(it)
        assertEquals(2020.99, arr.pos(it.size - 1), "chandelierExit last with different parameters")
    }
}