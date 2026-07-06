package strategy

import data.DataSet
import data.SectorIndexToETF
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.indicators.AverageDirectionalIndex
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

    private lateinit var rsi: Map<String, FloatSeries>
    private lateinit var adi: Map<String, FloatSeries>
    private lateinit var rsi14: Map<String, FloatSeries>
    private lateinit var rsi14index: FloatSeries

    override fun eval(data: DataSet, index: Int) {
        if (index == 0) {
            // TODO add init() before eval
            // Init
            rsi = data.lists.associate { Pair(it.symbol, RSI(3).eval(it)) }
            rsi14 = data.lists.associate { Pair(it.symbol, RSI(14).eval(it)) }
            adi = data.lists.associate { Pair(it.symbol, AverageDirectionalIndex().eval(it)) }
            rsi14index  = RSI(14).eval(data.index!!)
        }

        // Sell all at end
        if (index == data.size - 1)
            closeAll(data, index)
        else
        {
            closeAll(data, index)

            val weights = indexWeightsByDate[data.lists[0][index].date]!!.toMutableMap()
            if (index >= 10) {

                data.lists.forEach { list ->
                    val currRsi = rsi[list.symbol]!![index]
                    if (currRsi <= 33.64 || currRsi >= 81.32) {
                        // Good
                        weights[list.symbol] = weights[list.symbol]!! * 1.1f
                    }
                    else if (currRsi in 52.97..67.64) {
                        // neutral

                    }
                    else {
                        // bad
                        weights[list.symbol] = weights[list.symbol]!! * 0.9f
                    }
                }

                data.lists.forEach { list ->
                    val curr = adi[list.symbol]!![index]
                    if (curr in 16.37..20.81 ||  curr in 25.30..31.48) {
                        // Good
                        weights[list.symbol] = weights[list.symbol]!! * 1.1f
                    }
                    else if (curr in 20.81..25.30) {
                        // neutral
                    }
                    else {
                        // bad
                        weights[list.symbol] = weights[list.symbol]!! * 0.9f
                    }
                }


                data.lists.forEach { list ->
                    val curr = rsi14[list.symbol]!![index] - rsi14index[index]
                    if (curr in -0.50..4.19 || curr < -9.88) {
                        // Good
                        weights[list.symbol] = weights[list.symbol]!! * 1.1f
                    }
                    else if (curr in -4.30..-0.50) {
                        // neutral
                    }
                    else {
                        // bad
                        weights[list.symbol] = weights[list.symbol]!! * 0.9f
                    }
                }
            }
            /*
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

             */

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


