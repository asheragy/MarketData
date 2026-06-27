package strategy

import data.DataSet
import data.SectorIndexToETF
import java.time.LocalDate

class SectorStrategy(indexes: DataSet) : Strategy() {
    private val indexWeightsByDate = HashMap<LocalDate, Map<String, Float>>()

    init {
        val closeByDate = HashMap<LocalDate, MutableMap<String, Float>>()

        for (table in indexes.lists) {
            val symbol = SectorIndexToETF[table.symbol] ?: table.symbol
            for (row in table) {
                closeByDate
                    .getOrPut(row.date) { HashMap() }[symbol] = row.close
            }
        }

        for ((date, closeBySymbol) in closeByDate) {
            val totalClose = closeBySymbol.values.sum()
            indexWeightsByDate[date] = closeBySymbol.mapValues { (_, close) -> close / totalClose }
        }

        val last = indexWeightsByDate[indexes.lists.first().last().date]
        last?.entries
            ?.sortedByDescending { it.value }
            ?.forEach { (symbol, weight) -> println("$symbol=$weight") }
    }

    override fun eval(data: DataSet, index: Int) {
        // Sell all at end
        if (index == data.size - 1)
            closeAll(data, index)
        else if (index % 100 == 0) {
            closeAll(data, index)

            val available = cash
            data.lists.forEach {
                val curr = it[index]

                val weight = indexWeightsByDate[curr.date]!![it.symbol]!!
                val amount = (available * weight).coerceAtMost(cash)
                open(it.symbol, curr, amount)
            }

        }
    }
}
