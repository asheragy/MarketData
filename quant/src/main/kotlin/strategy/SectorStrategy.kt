package strategy

import data.DataSet
import data.SectorIndexToETF
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.indicators.RSI
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

    private lateinit var rsi: Map<String, FloatArray>

    override fun eval(data: DataSet, index: Int) {
        if (index == 0) {
            // TODO add init() before eval
            // Init
            rsi = data.lists.associate { Pair(it.symbol, RSI(5).eval(it)) }
        }

        // Sell all at end
        if (index == data.size - 1)
            closeAll(data, index)
        else
        {
            closeAll(data, index)

            val weights = indexWeightsByDate[data.lists[0][index].date]!!.toMutableMap()
            /*
            if (index >= 10) {
                val currRsi = data.lists.associate { Pair(it.symbol, rsi[it.symbol]!![index]) }
                val sorted = currRsi.toList().sortedByDescending { it.second }

                var weight = 1.25f
                sorted.forEach {
                    weights[it.first] = weights[it.first]!! * weight
                    weight -= 0.05f
                }
            }
             */
            if (index < data.lists[0].size - 2) {
                val change = data.lists.associate {
                    val curr = it[index]
                    val next = it[index + 1]
                    val diff = next.getPercentDiff(curr)

                    Pair(it.symbol, diff)
                }
                val sorted = change.toList().sortedByDescending { it.second }

                var weight = 1.25f
                sorted.forEach {
                    weights[it.first] = weights[it.first]!! * weight
                    weight -= 0.05f
                }
            }

            normalize(weights)

            val available = cash
            data.lists.forEach {
                val curr = it[index]

                val weight = weights[it.symbol]!!
                val amount = available.weighted(weight)
                open(it.symbol, curr, amount)
            }

        }
    }
}

fun normalize(weights: MutableMap<String, Float>) {
    val total = weights.values.sum()

    weights.forEach { (symbol, weight) ->
        weights[symbol] = weight / total
    }
}


