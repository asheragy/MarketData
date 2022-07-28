package data

import org.cerion.marketdata.core.model.OHLCVTable


data class DataSet(val lists: List<OHLCVTable>, val index: OHLCVTable? = null) {

    val size = lists.first().size

    init {
        val first = lists.first()
        lists.forEach { assertEqualRange(it, first) }
        index?.also { assertEqualRange(it, first) }
    }

    fun getBySymbol(symbol: String): OHLCVTable? {
        var match = lists.firstOrNull { it.symbol == symbol }
        if (match == null && index?.symbol == symbol)
            match = index

        return match
    }

    override fun toString(): String {
        return "lists=" + lists.map { it.symbol } + " size=" + size + " index=" + (index?.symbol ?: "null")
    }

    private fun assertEqualRange(tableA: OHLCVTable, tableB: OHLCVTable) {
        if (!tableA.equalRange(tableB))
            throw IllegalArgumentException("Range of all tables must match")
    }

    companion object {
        fun getNormalizedDataSet(lists: List<OHLCVTable>, index: OHLCVTable? = null): DataSet {
            var startDates = lists.map { it[0].date }
            var endDates = lists.map { it[it.size - 1].date }
            if (index != null) {
                startDates = startDates + index[0].date
                endDates = endDates + index[index.size - 1].date
            }

            val startDate = startDates.max()
            val endDate = endDates.min()

            return DataSet(lists.map { it.truncate(startDate, endDate) }, index?.truncate(startDate, endDate))
        }
    }
}