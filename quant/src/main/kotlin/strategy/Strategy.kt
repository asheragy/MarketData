package strategy

import data.DataSet
import model.Trade
import model.Position

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
            val current = data.getBySymbol(position.symbol)!![index]
            val trade = position.close(current)
            _trades.add(trade)
        }

        positions.clear()
    }
}