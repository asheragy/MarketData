package train

import data.SectorETFDef
import data.TextDataRepository
import org.cerion.marketdata.core.indicators.*
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.ExpMovingAverage


/*
| Quantile | Indicator Range | Avg Indicator | Avg Excess | Median Excess | Win Rate |
| -------: | --------------: | ------------: | ---------: | ------------: | -------: |
|    0-10% |     18.2 - 27.4 |          23.1 |      0.42% |         0.18% |      53% |
|   10-20% |     27.4 - 34.8 |          31.2 |      0.21% |         0.05% |      51% |
|      ... |             ... |           ... |        ... |           ... |      ... |
|  90-100% |     74.9 - 89.3 |          81.5 |     -0.38% |        -0.22% |      46% |
 */

// TODO conditional output, only care about compute value if other conditions are true
class InputData<T, U>(
    val name: String,
    val init: (OHLCVTable) -> T,
    val initIndex: ((OHLCVTable) -> U),
    val compute: (indexCache: U, cache: T, curr: OHLCVTable, index: Int) -> Float,
    val buckets: Int = 5)

data class RunResult(val input: InputData<*, *>, val buckets: List<Bucket>, val lookahead: Int) {
    val rankFirstBucket = buckets.first { it.rank == 1 }
    val rankLastBucket = buckets.maxBy { it.rank }
    val avgGainSpread = rankFirstBucket.averageGain - rankLastBucket.averageGain
    val medianGainSpread = rankFirstBucket.medianGain - rankLastBucket.medianGain
    val winRateSpread = rankFirstBucket.winRate - rankLastBucket.winRate
    var score = 0.0

    fun print() {
        println("LA:$lookahead S:${score.decimal2()} ${input.name}")
        Table.print(
            rows = buckets,
            columns = listOf(
                TableColumn("Indicator Range", Align.CENTER) { bucket ->
                    "${bucket.rangeStart.toDouble().decimal2()} - ${bucket.rangeEnd.toDouble().decimal2()}"
                },
                TableColumn("Avg Indicator", Align.RIGHT) { bucket ->
                    bucket.averageInd.decimal2()
                },
                TableColumn("Avg Gain", Align.RIGHT) { bucket ->
                    bucket.averageGain.decimal2()
                },
                TableColumn("Median Gain", Align.RIGHT) { bucket ->
                    bucket.medianGain.decimal2()
                },
                TableColumn("Win Rate", Align.RIGHT) { bucket ->
                    bucket.winRate.percent2()
                },
                TableColumn("Rank", Align.CENTER) { bucket ->
                    bucket.rank.toString()
                },
            )
        )
        println()
    }
}

fun main() {
    val dataSource = TextDataRepository()
    val dataSet = dataSource.get(SectorETFDef())
    val index = dataSet.index!!
    val runs = mutableListOf<RunResult>()

    val inputs = listOf(
        InputData(
            "RSI 3",
            init = { table -> RSI(3).eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "RSI 7",
            init = { table -> RSI(7).eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "RSI 14",
            init = { table -> RSI(14).eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "RSI 14 - RSI 14[1 period ago]",
            init = { table -> RSI(14).eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] - cache[index - 1] }
        ),
        InputData(
            "RSI 7 - 14",
            init = { table -> Pair(RSI(7).eval(table), RSI(14).eval(table)) },
            initIndex = {},
            compute = { _, cache, _, index -> cache.first[index] - cache.second[index] }
        ),
        InputData(
            "RSI(14) / EMA(RSI(14), 3)",
            init = { table ->
                val rsi = RSI(14).eval(table)
                val ema = ExpMovingAverage(3).eval(rsi)
                Pair(rsi, ema)
            },
            initIndex = {},
            compute = { _, cache, _, index -> cache.first[index] / cache.second[index] },
        ),
        InputData(
            "RSI(14) - EMA(RSI(14), 3)",
            init = { table ->
                val rsi = RSI(14).eval(table)
                val ema = ExpMovingAverage(3).eval(rsi)
                Pair(rsi, ema)
            },
            initIndex = {},
            compute = { _, cache, _, index -> cache.first[index] - cache.second[index] },
        ),
        InputData(
            "RSI 14 Bollinger Bands",
            init = { table -> RSI(14).eval(table).bb(20, 2.0f) },
            initIndex = {},
            compute = { _, cache, _, index -> cache.percent(index) }
        ),
        InputData(
            "RSI(stock, 14) - RSI(SPY, 14)",
            init = { table -> RSI(14).eval(table) },
            initIndex = { index -> RSI(14).eval(index) },
            compute = { indexRsi, rsi, _, i -> rsi[i] - indexRsi[i]  }
        ),
        InputData(
            "Conditional low/high diff",
            init = { table ->
                val rsi = RSI(14).eval(table)
                val ema = ExpMovingAverage(3).eval(rsi)
                Pair(rsi, ema)
            },
            initIndex = {},
            buckets = 3,
            compute = { _, cache, _, index ->
/*
RSI(14) is low
AND
RSI(14) - EMA(RSI(14), 3) is positive

opposite
RSI(14) is high
AND
RSI(14) - EMA(RSI(14), 3) is negative
 */
                // TODO should bucket into 3 groups
                val rsi = cache.first[index]
                val diff = rsi - cache.second[index]
                if (diff > 0)
                    rsi
                else
                    -rsi
            }
        ),
        InputData(
            "RSI(14) when price <> EMA(price, 50)", // Would be EMA200 for daily
            init = { table ->
                val rsi = RSI(14).eval(table)
                val ema = ExpMovingAverage(50).eval(table)
                Triple(rsi, ema, table.close)
            },
            initIndex = { },
            buckets = 3,
            compute = { _, cache, _, i ->
                val rsi = cache.first[i]
                val ema = cache.second[i]
                val price = cache.third[i]
                if (price < ema)
                    -rsi
                else
                    rsi
            }
        ),

        // TODO should be able to calculate RSI on a FloatArray
        // Also this needs init to take both
        // RSI(stock / SPY, 14) // need index to calculate new closing array
        InputData(
            "True Strength Index",
            init = { table -> TrueStrengthIndex().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "TRIX",
            init = { table -> TRIX().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "Stochastic",
            init = { table -> Stochastic().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "Prings Know Sure Thing",
            init = { table -> PringsKnowSureThing().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "Commodity Channel Index",
            init = { table -> CommodityChannelIndex().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "Chaikin Money Flow",
            init = { table -> ChaikinMoneyFlow().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        ),
        InputData(
            "Average Directional Index",
            init = { table -> AverageDirectionalIndex().eval(table) },
            initIndex = {},
            compute = { _, cache, _, index -> cache[index] }
        )

    )

    for (rawInput in inputs) {
        val input = rawInput as InputData<Any, Any>
        val indexCache = input.initIndex(index)
        val resultsAll = arrayListOf<Pair<Float, Float>>()

        for (table in dataSet.lists) {
            val cache = input.init(table)
            val resultsMap = hashMapOf<Int, ArrayList<Pair<Float, Float>>>()

            for (lookahead in listOf(1,2,3)) {
                val results = resultsMap.getOrPut(lookahead) { arrayListOf() }

                for (i in 20 until table.size - 1 - lookahead) {
                    val p1 = index[i + lookahead].getPercentDiff(index[i])
                    val p2 = table[i + lookahead].getPercentDiff(table[i])
                    val ind = input.compute(indexCache, cache, table, i)

                    val result = Pair(ind, p2 - p1)
                    results.add(result)
                    resultsAll.add(result)
                }
            }

            resultsMap.forEach { (lookahead, results) ->
                val buckets = createBuckets(results, input.buckets)
                runs.add(RunResult(input, buckets, lookahead))
            }
        }

        val buckets = createBuckets(resultsAll, input.buckets)
        runs.add(RunResult(input, buckets, 0))
    }

    val avgGainSpreads = Pair(runs.minOf { it.avgGainSpread }, runs.maxOf { it.avgGainSpread })
    val medGainSpreads = Pair(runs.minOf { it.medianGainSpread }, runs.maxOf { it.medianGainSpread })
    val winRateSpreads = Pair(runs.minOf { it.winRateSpread }, runs.maxOf { it.winRateSpread })

    runs.forEach { run ->
        val normalizedAvgGainSpread =
            (run.avgGainSpread - avgGainSpreads.first) /
                    (avgGainSpreads.second - avgGainSpreads.first)

        val normalizedMedGainSpread =
            (run.medianGainSpread - medGainSpreads.first) /
                    (medGainSpreads.second - medGainSpreads.first)

        val normalizedWinRateSpread =
            (run.winRateSpread - winRateSpreads.first) /
                    (winRateSpreads.second - winRateSpreads.first)

        run.score = normalizedAvgGainSpread + normalizedMedGainSpread + normalizedWinRateSpread
    }

    runs.filter { x -> x.lookahead == 0 }.sortedBy{ it.score }.forEach { run -> run.print() }
}



