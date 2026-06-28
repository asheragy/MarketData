package strategy

import data.DataSet
import model.Money
import model.Position
import model.Trade
import org.cerion.marketdata.core.model.OHLCVRow

abstract class Strategy {
    private val _positions = mutableListOf<Position>()
    val positions: List<Position>
        get() = _positions

    private val _trades = mutableListOf<Trade>()
    val trades: List<Trade>
        get() = _trades

    val startingCash = Money.of(1000.0)
    var cash: Money = startingCash
        private set

    val profit: Money
        get() = (cash - startingCash)

    abstract fun eval(data: DataSet, index: Int)

    protected fun open(symbol: String, buy: OHLCVRow, amount: Money) {
        if (amount > cash)
            throw IllegalArgumentException("Not enough money {$amount} > {$cash}")

        _positions.add(Position(symbol, buy, amount.maxShares(buy)))
        cash -= amount
    }

    protected fun close(position: Position, data: DataSet, index: Int) {
        val current = data.getBySymbol(position.symbol)!![index]
        val trade = position.close(current)
        _trades.add(trade)
        cash += trade.proceeds

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
            cash += trade.proceeds
        }

        _positions.clear()
    }
}
