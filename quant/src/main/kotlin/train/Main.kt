package train

import data.SectorETFDef
import data.TextDataRepository
import util.*


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

        if (buckets.first().condition != null) {
            columns.add(0,
                TableColumn("Count", Align.RIGHT) { bucket: Bucket ->
                    bucket.list.size.toString()
                }
            )
            columns.add(1,
                TableColumn("Condition", Align.RIGHT) { bucket: Bucket ->
                    bucket.condition ?: ""
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


    val ctxMap = dataSet.lists.map { EvalContext(it, dataSet.index) }.associateBy { it.table.symbol }

    for (input in Inputs) {
        val resultsAll = arrayListOf<Triple<String, Float, Float>>()
        for (table in dataSet.lists) {
            //val resultsMap = hashMapOf<Int, ArrayList<Pair<Float, Float>>>()
            val ctx = ctxMap[table.symbol]!!
            // TODO cache is a bit less important here, useful for core computations like RSI(14) but dont need to save everything
            val exprEval = ctx.eval(input.expr)
            val labels = if(input.expr is CustomExpr) input.expr.labels else listOf()

            for (lookahead in listOf(1)) {
                //val results = resultsMap.getOrPut(lookahead) { arrayListOf() }

                for (i in 20 until table.size - 1 - lookahead) {
                    val p1 = index[i + lookahead].getPercentDiff(index[i])
                    val p2 = table[i + lookahead].getPercentDiff(table[i])
                    val ind = exprEval[i]

                    val label = if(labels.isEmpty()) "" else labels[i]
                    val result = Triple(label, ind, p2 - p1)
                    //results.add(result)
                    resultsAll.add(result)
                }
            }

            /* TODO this is in the wrong spot
            resultsMap.forEach { (lookahead, results) ->
                val buckets = createBuckets(results, input.bucketBy)
                runs.add(RunResult(input, buckets, lookahead))
            }
             */
        }

        val buckets = createBuckets(resultsAll, input.quantiles)
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


