package strategy

import data.DataSet
import model.Position
import org.cerion.marketdata.core.indicators.RSI

/**
 * Open position is lowest RSI
 */
class RsiStrategy : Strategy() {

    override fun eval(data: DataSet, index: Int) {
        val lowest = data.lists.map { Pair(it, RSI().eval(it)[index]) }.minBy { it.second }
        val highest = data.lists.map { Pair(it, RSI().eval(it)[index]) }.maxBy { it.second }

        if (positions.isEmpty())
            open(lowest.first.symbol, lowest.first[index], cash)
        else {
            // Only swap current position if it's the highest
            if (positions[0].symbol == highest.first.symbol && (highest.second - lowest.second) > 10.0) {
                closeAll(data, index)

                open(lowest.first.symbol, lowest.first[index], cash)
            }
        }

    }
}