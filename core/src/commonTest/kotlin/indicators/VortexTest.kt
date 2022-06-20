package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class VortexTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val arr = Vortex(14).eval(it)
        assertEquals(1.0, arr.neg(0), "vortex (-) 0")
        assertEquals(1.0, arr.pos(0), "vortex (+) 0")
        assertEquals(0.83, arr.neg(1), "vortex (-) 0")
        assertEquals(0.17, arr.pos(1), "vortex (+) 0")
        assertEquals(1.08, arr.neg(27), "vortex (-) 2p-1")
        assertEquals(0.92, arr.pos(27), "vortex (+) 2p-1")
        assertEquals(1.10, arr.neg(28), "vortex (-) 2p")
        assertEquals(0.96, arr.pos(28), "vortex (+) 2p")
        assertEquals(0.98, arr.neg(arr.size - 1), "vortex (-) last")
        assertEquals(0.94, arr.pos(arr.size - 1), "vortex (+) last")
        assertEquals(-0.04, arr.diff(arr.size - 1), "vortex diff last")
    }
}