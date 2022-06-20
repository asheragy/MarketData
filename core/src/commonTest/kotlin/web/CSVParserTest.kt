package org.cerion.marketdata.core.web

import kotlin.test.Test
import kotlin.test.assertEquals


internal class CSVParserTest {

    @Test
    fun parseLine_allPricesEqual() {
        val p = CSVParser.parseLine("2017-02-17,12.01,12.01,12.01,12.01,12.01,000")

        assertEquals(12.01f, p.open, "open")
        assertEquals(12.01f, p.high, "high")
        assertEquals(12.01f, p.low, "low")
        assertEquals(12.01f, p.close, "close")
    }

    @Test
    fun parseLine_allPricesEqual_adjClose() {
        val p = CSVParser.parseLine("2017-02-17,12.01,12.01,12.01,12.01,11.973215,000")

        assertEquals(11.973215f, p.open)
        assertEquals(11.973215f, p.high)
        assertEquals(11.973215f, p.low)
        assertEquals(11.973215f, p.close)
    }
}