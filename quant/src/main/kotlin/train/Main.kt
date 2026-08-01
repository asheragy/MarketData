package train

import data.SectorETFDef
import data.TextDataRepository
import org.cerion.marketdata.core.indicators.*
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.series.FloatSeries
import kotlin.math.absoluteValue


/*
| Quantile | Indicator Range | Avg Indicator | Avg Excess | Median Excess | Win Rate |
| -------: | --------------: | ------------: | ---------: | ------------: | -------: |
|    0-10% |     18.2 - 27.4 |          23.1 |      0.42% |         0.18% |      53% |
|   10-20% |     27.4 - 34.8 |          31.2 |      0.21% |         0.05% |      51% |
|      ... |             ... |           ... |        ... |           ... |      ... |
|  90-100% |     74.9 - 89.3 |          81.5 |     -0.38% |        -0.22% |      46% |
 */

// TODO conditional output, only care about compute value if other conditions are true
class InputData(
    val expr: SeriesExpr,
    val split: ((List<Pair<Float, Float>>) -> Map<String, List<Pair<Float, Float>>>)? = null,
    val buckets: Int = 5)

data class RunResult(val input: InputData, val buckets: List<Bucket>, val lookahead: Int) {
    val rankFirstBucket = buckets.first { it.rank == 1 }
    val rankLastBucket = buckets.maxBy { it.rank }
    val avgGainSpread = rankFirstBucket.averageGain - rankLastBucket.averageGain
    val medianGainSpread = rankFirstBucket.medianGain - rankLastBucket.medianGain
    val winRateSpread = rankFirstBucket.winRate - rankLastBucket.winRate
    var score = 0.0

    fun print() {
        val name = input.expr.toString()
        println("LA:$lookahead S:${score.decimal2()} ${name}")
        val columns = mutableListOf(
            TableColumn("Indicator Range", Align.CENTER) { bucket: Bucket ->
                bucket.rangeLabel
            },
            TableColumn("Avg Indicator", Align.RIGHT) { bucket: Bucket ->
                bucket.averageInd.decimal2()
            },
            TableColumn("Avg Gain", Align.RIGHT) { bucket: Bucket ->
                bucket.averageGain.decimal2()
            },
            TableColumn("Median Gain", Align.RIGHT) { bucket: Bucket ->
                bucket.medianGain.decimal2()
            },
            TableColumn("Win Rate", Align.RIGHT) { bucket: Bucket ->
                bucket.winRate.percent2()
            }
        )

        if (input.split != null) {
            columns.add(0,
                TableColumn("Count", Align.RIGHT) { bucket: Bucket ->
                    bucket.list.size.toString()
                }
            )
        }

        columns.add(
            TableColumn("Rank", Align.CENTER) { bucket: Bucket ->
                bucket.rank.toString()
            }
        )

        Table.print(
            rows = buckets,
            columns = columns
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
        InputData(expr = FuncExpr(RSI(3))),
        InputData(expr = FuncExpr(RSI(7))),
        InputData(expr = FuncExpr(RSI(14))),
        InputData(expr = FuncExpr(RSI(14)) - LagExpr(FuncExpr(RSI(14)), 1)),
        InputData(expr = FuncExpr(RSI(7)) - FuncExpr(RSI(14))),
        InputData(expr = FuncExpr(RSI(14)) - FuncExpr(IndexTable, RSI(14))),
        InputData(expr = FuncExpr(RSI(14)) / FuncExpr(FuncExpr(RSI(14)), ExpMovingAverage(3))),
        InputData(expr = FuncExpr(RSI(14)) - FuncExpr(FuncExpr(RSI(14)),  ExpMovingAverage(3))),
        InputData(expr = FuncExpr(TrueStrengthIndex())),
        InputData(expr = FuncExpr(TRIX())),
        InputData(expr = FuncExpr(Stochastic())),
        InputData(expr = FuncExpr(PringsKnowSureThing())),
        InputData(expr = FuncExpr(CommodityChannelIndex())),
        InputData(expr = FuncExpr(ChaikinMoneyFlow())),
        InputData(expr = FuncExpr(AverageDirectionalIndex())),

        InputData(expr = CustomExpr("Conditional low/high diff", eval = { ctx ->
            val rsi = RSI(14).eval(ctx.table)
            val ema = ExpMovingAverage(3).eval(rsi)

            val result = FloatSeries(ctx.table.size)
            for(i in ctx.table.indices) {
                val diff = rsi[i] - ema[i]
                // RSI(14) is low AND RSI(14) - EMA(RSI(14), 3) is positive
                if (rsi[i] < 50 && diff > 0)
                    result[i] = rsi[i]
                // RSI(14) is high AND RSI(14) - EMA(RSI(14), 3) is negative
                else if (rsi[i] > 50 && diff < 0)
                    result[i] = -rsi[i]
                else
                    result[i] = 0.0f
            }

            result
        }),
            split = { result ->
                val pos = result.filter { it.first > 0 }.sortedBy { it.first }
                val neg = result.filter { it.first < 0 }.sortedBy { it.first }
                val zero = result.filter { it.first == 0.0f }
                mapOf(Pair("Low RSI cross", pos), Pair("High RSI cross", neg), Pair("No match", zero))
            }),

        InputData(
            // Would be EMA200 for daily
            expr = CustomExpr("RSI(14) when price <> EMA(price, 50)", { ctx ->
                val rsi = RSI(14).eval(ctx.table)
                val ema = ExpMovingAverage(50).eval(ctx.table)
                val price = ctx.table.close

                val result = FloatSeries(ctx.table.size)
                for(i in ctx.table.indices) {
                    if (price[i] < ema[i])
                        result[i] = -rsi[i]
                    else
                        result[i] = rsi[i]
                }

                result
            }),
            split = { result ->
                val pos = result.filter { it.first > 0 }.sortedBy { it.first }.splitIntoExactly(2)
                val neg = result.filter { it.first < 0 }.sortedBy { it.first }.splitIntoExactly(2)
                mapOf(
                    Pair("Above EMA [${pos[0].first().first.decimal2()}, ${pos[0].last().first.decimal2()}]", pos[0]),
                    Pair("Above EMA [${pos[1].first().first.decimal2()}, ${pos[1].last().first.decimal2()}]", pos[1]),
                    Pair("Below EMA [${neg[0].first().first.absoluteValue.decimal2()}, ${neg[0].last().first.absoluteValue.decimal2()}]", neg[0]),
                    Pair("Below EMA [${neg[1].first().first.absoluteValue.decimal2()}, ${neg[1].last().first.absoluteValue.decimal2()}]", neg[1]),
                )
            }
        ),
        InputData(
            expr = CustomExpr("RSI 14 Bollinger Bands", { ctx ->
                RSI(14).eval(ctx.table).bb(20, 2.0f).percent()
            })
        ),

        InputData(expr = CustomExpr("RSI(stock / SPY, 14)", eval = { ctx ->
            val series = ctx.table.close.divide(ctx.index.close)
            RSI(14).eval(series)
        }))
    )

    val ctxMap = dataSet.lists.map { EvalContext(it, dataSet.index) }.associateBy { it.table.symbol }

    for (input in inputs) {
        val resultsAll = arrayListOf<Pair<Float, Float>>()

        for (table in dataSet.lists) {
            val resultsMap = hashMapOf<Int, ArrayList<Pair<Float, Float>>>()
            val ctx = ctxMap[table.symbol]!!
            // TODO cache is a bit less important here, useful for core computations like RSI(14) but dont need to save everything
            val exprEval = ctx.eval(input.expr)

            for (lookahead in listOf(1)) {
                val results = resultsMap.getOrPut(lookahead) { arrayListOf() }

                for (i in 20 until table.size - 1 - lookahead) {
                    val p1 = index[i + lookahead].getPercentDiff(index[i])
                    val p2 = table[i + lookahead].getPercentDiff(table[i])
                    val ind = exprEval[i]

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

        val buckets = if (input.split != null) createBuckets(resultsAll, input.split) else createBuckets(resultsAll, input.buckets)
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


