package model

import org.cerion.marketdata.core.model.OHLCVRow
import utils.diff

data class Trade(val symbol: String, val buy: OHLCVRow, val sell: OHLCVRow, val shares: Double) {
    val costBasis: Money
        get() {
            return Money.buy(shares * buy.close)
        }

    val proceeds: Money
        get() {
            return Money.sell(shares * sell.close)
        }

    override fun toString(): String {
        val profit = proceeds - costBasis

        val fields: List<Any> = listOf(
            symbol.padEnd(8, ' '),
            buy.date.toString(),
            "%dd".format(sell.date.diff(buy.date)),
            costBasis,
            proceeds,
            profit.toString().padStart(6, ' '),
            "%.2f".format(sell.getPercentDiff(buy)).padStart(6, ' ') + "%",
        )

        return fields.joinToString("\t")
    }
}
