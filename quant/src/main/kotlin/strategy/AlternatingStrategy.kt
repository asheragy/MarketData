package strategy

import data.DataSet
import model.Position

/**
 * Every 100 ticks buy next in list
 */
class AlternatingStrategy : Strategy() {
    private var listIndex = 0

    override fun eval(data: DataSet, index: Int) {
        // Every 100 days alternate
        if (index % 100 == 0) {
            closeAll(data, index)

            val list = data.lists[listIndex++ % data.lists.size]
            open(list.symbol, list[index], cash)
        }

        // Sell all at end
        if (index == data.size - 1)
            closeAll(data, index)
    }
}