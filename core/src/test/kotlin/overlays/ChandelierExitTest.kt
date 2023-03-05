package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class ChandelierExitTest : TestBase() {

    @Test
    fun eval() {
        var arr = ChandelierExit(22, 3.0).eval(table)

        assertNotSet(arr.pos(0))
        assertNotSet(arr.pos(20))
        assertEquals(1379.12, arr.pos(21), "chandelierExit 21")
        assertEquals(1374.90, arr.pos(22), "chandelierExit 22")
        assertEquals(2007.38, arr.pos(3950), "chandelierExit 3950")
        assertEquals(2030.88, arr.last.positive, "chandelierExit last")

        arr = ChandelierExit(15, 2.5).eval(table)
        assertEquals(2020.99, arr.last.positive, "chandelierExit last with different parameters")
    }
}