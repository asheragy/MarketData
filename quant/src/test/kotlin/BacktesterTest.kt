import data.DataSet
import org.cerion.marketdata.core.model.OHLCVTable
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

    /**
     * Hold but only 50% of the time
     */
    @Test
    fun partialHoldTime() {
        val strategy = object: Strategy() {
            override fun eval(data: DataSet, index: Int) {
                if (index % 500 == 0) {
                    val percent = 1.0 / data.lists.size
                    val currentCash = cash
                    data.lists.forEach {
                        open(it.symbol, it[index], currentCash * percent)
                    }
                }

                // Half way through sell all
                if (index % 500 == 490)
                    closeAll(data, index)

                // Sell all at end
                if (index == data.size - 1)
                    closeAll(data, index)
            }
        }

        val testResult = Backtester.run(data, strategy)

        //assertEquals(39, testResult.trades.size)
        assertEquals(1.3927826, testResult.strategyProfit, 0.005)
    }

    /**
     * Tests overlapping hold periods with different buy/sell times
     * Pattern will look like this
     *  --- --- --- -
     * - --- --- ---
     * -- --- --- ---
    */
    @Test
    fun concurrentTrades() {
        val strategy = object: Strategy() {
            override fun eval(data: DataSet, index: Int) {
                /**
                 * lists[0] held if i % 4 != 0
                 * lists[1] held if i % 4 != 1
                 * lists[2] held if i % 4 != 2
                 * all held if i % 4 == 3
                 */

                // Process sell (free up cash first)
                for(i in 0 until 1) {
                    val list = data.lists[i]
                    val position = positions.find { it.symbol == list.symbol }
                    if (index % 50 == i && position != null)
                        close(position, data, index)
                }

                // Process buy
                val buys = mutableListOf<OHLCVTable>()
                for(i in 0 until 1) {
                    val list = data.lists[i]
                    val position = positions.find { it.symbol == list.symbol }
                    if (index % 50 != i && position == null)
                        buys.add(list)
                }

                val cashPerTrade = cash
                buys.forEach {
                    open(it.symbol, it[index], cashPerTrade)
                }

                // Sell all at end
                if (index == data.size - 1)
                    closeAll(data, index)
            }
        }

        val testResult = Backtester.run(data, strategy)
        //assertEquals(925, testResult.trades.size)
        assertEquals(3.5143908, testResult.strategyProfit, 0.005)
    }
}