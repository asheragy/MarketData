import data.DataSet
import model.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import strategy.Strategy

class BacktesterTest {

    /**
     * Verify long term holding == multiple trades split up over same period
     */
    @Test
    fun compoundedProfits() {
        val data = Utils.getTestDataSet()

        val strategy = object: Strategy() {
            override fun eval(data: DataSet, index: Int) {
                // Every 100 ticks buy/sell the same
                if (index % 100 == 0) {
                    closeAll(data, index)
                    val list = data.index!!
                    positions.add(Position(list.symbol, list[index], 1.0))
                }

                // Sell all at end
                if (index == data.size - 1)
                    closeAll(data, index)
            }
        }

        val testResult = Backtester.run(data, strategy)

        assertEquals(13, testResult.trades.size)
        assertEquals(392.39816, testResult.indexProfit.toDouble(), 0.005)
        assertEquals(392.39816, testResult.strategyProfit.toDouble(), 0.005)
    }
}