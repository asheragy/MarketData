import data.DataSet
import model.Money
import model.Trade
import strategy.Strategy
import utils.diff
import kotlin.math.pow

object Backtester {

    fun run(dataSet: DataSet, strategy: Strategy): BackTestResult {
        val index = dataSet.index!!
        // 274 -> 740
        val indexBought = strategy.startingCash.maxShares(index.first())
        val indexSold = index.last().close * indexBought
        val indexProfit = Money.sell(indexSold) - strategy.startingCash

        for(i in 0 until dataSet.size)
            strategy.eval(dataSet, i)

        val startingMoney = strategy.startingCash
        val trades = strategy.trades.sortedBy { it.buy.date }

        /*
        var money = startingMoney
        trades.forEach {
            money += it.proceeds
        }
         */

        val strategyProfit = strategy.profit

        return BackTestResult(trades,
            index.last().date.diff(index.first().date),
            strategy.startingCash,
            indexProfit, strategyProfit)
    }
}

data class BackTestResult(val trades: List<Trade>,
                          val days: Int,
                     val startingCash: Money,
                     val indexProfit: Money,
                     val strategyProfit: Money) {

    fun annualizedReturn(profit: Money): String {
        val totalReturn = profit.amount.toDouble() / startingCash.amount.toDouble()
        val percent = (1.0 + totalReturn).pow(365.0 / days) - 1.0
        return "%.2f".format(percent * 100)
    }

    fun print() {
        trades.forEach { println(it) }

        println("Index Profit    = $indexProfit\t${indexProfit.percent(startingCash)}\t${annualizedReturn(indexProfit)}%/yr")
        println("Strategy Profit = $strategyProfit\t${strategyProfit.percent(startingCash)}\t${annualizedReturn(strategyProfit)}%/yr")
    }
}
