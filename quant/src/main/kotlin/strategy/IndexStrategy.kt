package strategy

import data.DataSet
import model.Position

/**
 * TODO this should really be a unit test
 */
class IndexStrategy : Strategy() {
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