package data

import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DataSetTest {

    @Test
    fun normalize() {
        val dates = List(10) { index -> LocalDate.of(2022, 1, index + 1) }
        val prices = dates.map { date -> OHLCVRow(date, 1f, 1f, 1f, 1f, 1000f) }

        val table1 = OHLCVTable("AA", prices)
        val table2 = OHLCVTable("BB", prices.subList(0, 8))
        val index = OHLCVTable("CC", prices.subList(2, 9))

        val dataSet = DataSet.getNormalizedDataSet(listOf(table1, table2), index)

        val startDate = dates[2]
        val endDate = dates[7]

        assertEquals(startDate, dataSet.lists[0].dates.first())
        assertEquals(startDate, dataSet.lists[1].dates.first())
        assertEquals(startDate, dataSet.index!!.dates.first())

        assertEquals(endDate, dataSet.lists[0].dates.last())
        assertEquals(endDate, dataSet.lists[1].dates.last())
        assertEquals(endDate, dataSet.index!!.dates.last())
    }
}