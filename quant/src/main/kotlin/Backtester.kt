import data.DataSet
import strategy.Strategy

object Backtester {


    fun run(dataSet: DataSet, strategy: Strategy) {
        val index = dataSet.index!!
        val indexProfit = index.last().getPercentDiff(index.first())

        for(i in 0 until dataSet.size)
            strategy.eval(dataSet, i)

        var tradeProfit = 0.0
        strategy.trades.forEach {
            // TODO need compounded profit
            tradeProfit += it.profit
            println(it)
        }

        println("Index Profit    = $indexProfit%")
        println("Strategy Profit = $tradeProfit%")

    }

}