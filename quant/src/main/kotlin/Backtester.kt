import data.DataSet
import model.Trade
import strategy.Strategy

object Backtester {

    fun run(dataSet: DataSet, strategy: Strategy): BackTestResult {
        val index = dataSet.index!!
        val indexProfit = index.last().getPercentDiff(index.first())

        for(i in 0 until dataSet.size)
            strategy.eval(dataSet, i)

        val startingMoney = 100.0
        var money = startingMoney
        val trades = strategy.trades.sortedBy { it.buy.date }
        trades.forEach {
            val profit = money * it.profit
            money += profit
        }

        val strategyProfit = 100 * (money - startingMoney) / startingMoney

        return BackTestResult(trades, indexProfit, strategyProfit)
    }
}

class BackTestResult(val trades: List<Trade>,
                     val indexProfit: Number,
                     val strategyProfit: Number) {

    fun print() {
        trades.forEach { println(it) }

        println("Index Profit    = $indexProfit%")
        println("Strategy Profit = $strategyProfit%")
    }
}