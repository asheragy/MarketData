import data.DataSet
import model.Trade
import strategy.Strategy

object Backtester {

    fun run(dataSet: DataSet, strategy: Strategy): BackTestResult {
        val index = dataSet.index!!
        // 274 -> 740
        val indexBought = strategy.startingCash / index.first().close
        val indexSold = index.last().close * indexBought
        val indexProfit = indexSold - strategy.startingCash

        for(i in 0 until dataSet.size)
            strategy.eval(dataSet, i)

        val startingMoney = strategy.startingCash
        var money = startingMoney
        val trades = strategy.trades.sortedBy { it.buy.date }
        trades.forEach {
            money += it.value
        }

        val strategyProfit = strategy.profit

        return BackTestResult(trades,
            strategy.startingCash,
            indexProfit, strategyProfit)
    }
}

class BackTestResult(val trades: List<Trade>,
                     val startingCash: Double,
                     val indexProfit: Double,
                     val strategyProfit: Double) {

    fun print() {
        trades.forEach { println(it) }

        println("Index Profit    = ${"%.2f".format(indexProfit)} / ${"%.2f".format(indexProfit / startingCash * 100)}%")
        println("Strategy Profit = ${"%.2f".format(strategyProfit)} / ${"%.2f".format(strategyProfit / startingCash * 100)}%")
    }
}