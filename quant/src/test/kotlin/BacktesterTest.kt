import data.DataSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import strategy.Strategy

class BacktesterTest {

    private val data = Utils.getTestDataSet()

    /**
     * Verify long term holding == multiple trades split up over same period
     */
    @Test
    fun compoundedProfits() {
        val strategy = object: Strategy() {
            override fun eval(data: DataSet, index: Int) {
                // Every 100 ticks buy/sell the same
                if (index % 100 == 0) {
                    closeAll(data, index)
                    val list = data.index!!
                    open(list.symbol, list[index], cash)
                }

                // Sell all at end
                if (index == data.size - 1)
                    closeAll(data, index)
            }
        }

        val testResult = Backtester.run(data, strategy)

        assertEquals(13, testResult.trades.size)
        assertEquals(3.9239816, testResult.indexProfit, 0.005)
        assertEquals(3.9239816, testResult.strategyProfit, 0.005)
    }

    /**
     * Buy equal percentage of each and hold til end
     */
    @Test
    fun percentageSplit() {
        val strategy = object: Strategy() {
            override fun eval(data: DataSet, index: Int) {
                if (index == 0) {
                    val percent = 1.0 / data.lists.size
                    val currentCash = cash
                    data.lists.forEach {
                        open(it.symbol, it[0], currentCash * percent)
                    }
                }

                // Sell all at end
                if (index == data.size - 1)
                    closeAll(data, index)
            }
        }

        val testResult = Backtester.run(data, strategy)

        assertEquals(3, testResult.trades.size)
        assertEquals(3.9239816, testResult.indexProfit, 0.005)
        assertEquals(3.5143908, testResult.strategyProfit, 0.005)
    }
}