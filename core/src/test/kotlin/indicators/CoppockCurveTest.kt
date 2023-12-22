package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import org.junit.Test

class CoppockCurveTest : TestBase() {

    @Test
    fun test() {
        val cc = CoppockCurve().eval(table22)
        assertNotSet(cc[0])
        assertNotSet(cc[22])
        assertEquals(-10.06, cc[23])
        assertEquals(-7.45, cc.last)
    }
}