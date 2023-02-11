package train

import data.SectorDataDef
import data.TextDataRepository
import org.cerion.marketdata.core.indicators.*
import org.cerion.marketdata.core.model.OHLCVTable

data class Result(val indicator: Float, val wins: Int)


fun main() {
    val dataSource = TextDataRepository()
    val dataSet = dataSource.get(SectorDataDef())
    val results = arrayListOf<Result>()
    val stats = mutableMapOf<Int, Int>()

    for(table in dataSet.lists) {
        //val indicator = TrueStrengthIndex().eval(table)
        //val indicator = TRIX().eval(table)
        //val indicator = Stochastic().eval(table)
        //val indicator = PringsKnowSureThing().eval(table)
        //val f = CommodityChannelIndex().eval(table)
        //val f = ChaikinMoneyFlow().eval(table)

        val f = AverageDirectionalIndex().eval(table)
        val indicator = f
        //val indicator = (0 until f.size).map { f.hist(it) }

        for(i in 20 until table.size - 10) {
            val wins = wins(table, i, 8)
            stats.increment(wins)

            val result = Result(indicator[i], wins)
            results.add(result)
        }
    }

    for(i in 0..8) {
        val bucketAverage = results.filter { it.wins == i }.map { it.indicator }.average()
        println("$i = $bucketAverage")
    }

    println(dataSet)
}

fun wins(table: OHLCVTable, index: Int, lookAhead: Int): Int {
    val curr = table[index]
    var count = 0
    for(i in 1..lookAhead) {
        val p = table[index+i]
        val diff = p.getPercentDiff(curr)
        if (diff > 0)
            count++
    }

    //println(count)
    return count
}

fun MutableMap<Int, Int>.increment(key: Int) {
    val curr = this[key]
    if (curr == null)
        this[key] = 1
    else
        this[key] = curr + 1
}