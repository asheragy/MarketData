package strategy

import data.DataSet
import model.Trade
import model.Position
import org.cerion.marketdata.core.model.OHLCVRow

abstract class Strategy {
    private val _positions = mutableListOf<Position>()
    val positions: List<Position>
        get() = _positions

    private val _trades = mutableListOf<Trade>()
    val trades: List<Trade>
        get() = _trades

    private val startingCash = 100.0
    var cash: Double = startingCash
        private set

    val profit: Double
        get() = (cash - startingCash) / startingCash

    abstract fun eval(data: DataSet, index: Int)

    protected fun open(symbol: String, buy: OHLCVRow, amount: Double) {
        if (amount > cash)
            throw IllegalArgumentException("Not enough money")

        _positions.add(Position(symbol, buy, amount / buy.close))
        cash -= amount
    }

    protected fun close(position: Position, data: DataSet, index: Int) {
        val current = data.getBySymbol(position.symbol)!![index]
        val trade = position.close(current)
        _trades.add(trade)
        cash += trade.value

        _positions.remove(position)
    }

    /**
     * Close all open positions
     */
    protected fun closeAll(data: DataSet, index: Int) {
        _positions.forEach { position ->
            // TODO call close() above
            val current = data.getBySymbol(position.symbol)!![index]
            val trade = position.close(current)
            _trades.add(trade)
            cash += trade.value
        }

        _positions.clear()
    }
}