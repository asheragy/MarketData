package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.TestBase
import org.junit.Test
import kotlin.test.assertEquals

class BalanceOfPowerTest : TestBase() {

    @Test
    fun test() {
        val bop = BalanceOfPower().eval(table)

        assertEquals(table.size, bop.size)
        assertEquals(-0.354, bop.first)
        assertEquals(-0.88, bop.last)
    }
}