import data.DataSet
import model.Trade
import strategy.Strategy

object Backtester {

    fun run(dataSet: DataSet, strategy: Strategy): BackTestResult {
        val index = dataSet.index!!
        val indexProfit = index.last().getPercentDiff(index.first()) / 100.0

        for(i in 0 until dataSet.size)
            strategy.eval(dataSet, i)

        val startingMoney = 100.0
        var money = startingMoney
        val trades = strategy.trades.sortedBy { it.buy.date }
        trades.forEach {
            money += it.value
        }

        val strategyProfit = strategy.profit

        return BackTestResult(trades, indexProfit, strategyProfit)
    }
}

class BackTestResult(val trades: List<Trade>,
                     val indexProfit: Double,
                     val strategyProfit: Double) {

    fun print() {
        trades.forEach { println(it) }

        println("Index Profit    = $indexProfit%")
        println("Strategy Profit = ${strategyProfit}%")
    }
}