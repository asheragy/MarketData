package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class AverageDirectionalIndexTest : TestBase() {

    @Test
    fun averageDirectionalIndex_defaults() = runPriceTest {
        val adx = AverageDirectionalIndex().eval(it) // period is 14 by default

        assertEquals(0.0, adx.first, "first")
        assertEquals(50.0, adx[1], "position 1")
        assertEquals(66.67, adx[2], "position 2")
        assertEquals(15.04, adx.last, "last")
    }

    @Test
    fun averageDirectionalIndex_test_7() = runPriceTest {
        val adx = AverageDirectionalIndex(7).eval(it)

        assertEquals(0.0, adx.first, "first")
        assertEquals(50.0, adx[1], "position 1")
        assertEquals(66.67, adx[2], "position 2")
        assertEquals(18.20, adx.last, "last")
    }

}