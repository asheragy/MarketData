package strategy

import data.DataSet
import org.cerion.marketdata.core.model.OHLCVRow

abstract class Strategy {
    protected val positions = mutableListOf<Position>()
    protected val _trades = mutableListOf<Trade>()

    val trades: List<Trade>
        get() = _trades

    abstract fun eval(data: DataSet, index: Int)

    /**
     * Close all open positions
     */
    protected fun closeAll(data: DataSet, index: Int) {
        positions.forEach { position ->
            val current = data.lists.first { it.symbol == position.symbol }[index]
            val trade = position.close(current)
            _trades.add(trade)
        }

        positions.clear()
    }

    data class Position(val symbol: String, val buy: OHLCVRow, val percentage: Double) {
        fun close(sell: OHLCVRow) = Trade(symbol, buy, sell, percentage)
    }

    data class Trade(val symbol: String, val buy: OHLCVRow, val sell: OHLCVRow, val percentage: Double) {
        val profit: Double
            get() = sell.getPercentDiff(buy) * percentage

        override fun toString(): String {
            return symbol + "\t" + buy.date + "\t" + sell.date.diff(buy.date) + "d\t" + sell.getPercentDiff(buy)
        }
    }
}