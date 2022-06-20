package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.Utils
import org.cerion.marketdata.core.platform.KMPDate
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import java.util.*
import kotlin.test.assertNull

class DividendHistoryTest : TestBase() {

    @Test
    fun dates() {
        val dividends = Utils.getDividends(9.1f, 2.2f, 3.56f)
        val history = DividendHistory(dividends, Utils.getDate(30))

        assertEquals(9.1, history.lastDividend!!.toFloat(), 0.005)
        assertEquals(KMPDate.TODAY, history.lastDividendDate)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        assertEquals(KMPDate(calendar.time), history.nextDividendEstimate!!)
    }

    @Test
    fun noDividends() {
        val history = DividendHistory(listOf(), KMPDate(2011, 8, 13))

        assertNull(history.lastDividend)
        assertEquals(0.0, history.totalDividends.toFloat(), 0.0001)
        assertNull(history.lastDividendDate)
        assertNull(history.nextDividendEstimate)
    }

    @Ignore("fails with UTC timezone")
    @Test
    fun startDate_pastLastDividend() {
        val history = DividendHistory(getSampleList(), KMPDate(2016, 5, 1))

        assertEquals(4.75, history.lastDividend!!.toFloat(), 0.0001)
        assertEquals(0.0, history.totalDividends.toFloat(), 0.0001)
        assertEquals(KMPDate(2016,4,12), history.lastDividendDate)
        assertEquals(KMPDate(2016,7,7), history.nextDividendEstimate)
    }

    @Ignore("fails with UTC timezone")
    @Test
    fun fields_Test() {
        val history = DividendHistory(getSampleList(), KMPDate(2011, 8, 13))

        assertEquals(4.75, history.lastDividend!!.toFloat(), 0.0001)
        assertEquals(89.3, history.totalDividends.toFloat(), 0.0001)
        assertEquals(KMPDate(2016,4,12), history.lastDividendDate)
        assertEquals(KMPDate(2016,7, 7), history.nextDividendEstimate)
    }

    private fun getSampleList(): List<Dividend> {
        val result = mutableListOf<Dividend>()

        val cal = GregorianCalendar(2010, 1, 1)

        for(i in 0..25) {
            cal.add(Calendar.DATE, 29 * 3) // Add 3 months but not exactly
            val date = cal.time

            result.add(Dividend(KMPDate(date), (i * 0.03f) + 4))
        }

        result.shuffle() // To test the fact this gets sorted in constructor
        return result
    }
}