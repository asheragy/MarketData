package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class ZigZagTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val arr = ZigZag(20.0).eval(it)

        assertEquals(it.high[0], arr[0], "zigzag start")
        assertEquals(it.low[307], arr[307], "zigzag first low")
        assertEquals(it.high[349], arr[349], "zigzag first high")
        assertEquals(it.low[2957], arr[2957], "zigzag last low")
        assertEquals(it.high[3868], arr[3868], "zigzag last high")
        assertEquals(it.low.last, arr.last, "zigzag end")
    }
}