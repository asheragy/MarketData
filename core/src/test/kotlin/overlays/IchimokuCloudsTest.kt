package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class IchimokuCloudsTest : TestBase() {

    @Test
    fun eval() = runPriceTest {
        val size = it.size
        var arr = IchimokuClouds(9, 26, 52).eval(it)

        assertEquals(2046.1051, arr.pos(size- 1), "ichimokuCloud SpanA last")
        assertEquals(2054.87, arr.neg(size - 1), "ichimokuCloud SpanB last")

        arr = IchimokuClouds(5, 15, 30).eval(it)
        assertEquals(2050.0, arr.pos(size - 1), "ichimokuCloud SpanA last with different parameters")
        assertEquals(2048.77, arr.neg(size - 1), "ichimokuCloud SpanB last with different parameters")
    }
}