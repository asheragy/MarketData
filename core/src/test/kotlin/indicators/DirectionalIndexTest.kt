package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test

class DirectionalIndexTest : TestBase() {

    @Test
    fun directionalIndex_defaults() = runPriceTest{
        val arr = DirectionalIndex().eval(it)
        val last = arr.size - 1

        // TODO verify values online, just doing these pre-refactor
        assertEquals(30.29, arr.neg(last), "last")
        assertEquals(27.01, arr.pos(last), "last")
    }
}