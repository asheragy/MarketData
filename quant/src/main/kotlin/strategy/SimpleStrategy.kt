package strategy

import data.DataSet

/**
 * Buy equal percentage of all at start
 */
class SimpleStrategy : Strategy() {

    override fun eval(data: DataSet, index: Int) {
        if (index == 0) {
            val percent = 1.0 / data.lists.size
            data.lists.forEach { positions.add(Position(it.symbol, it[0], percent)) }
        }

        // Sell all at end
        if (index == data.size - 1)
            closeAll(data, index)
    }
}