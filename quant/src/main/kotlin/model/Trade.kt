package model

import org.cerion.marketdata.core.model.OHLCVRow
import utils.diff

data class Trade(val symbol: String, val buy: OHLCVRow, val sell: OHLCVRow, val shares: Double) {
    val value: Double
        get() {
            return shares * sell.close
        }

    override fun toString(): String {
        val diff = shares * (sell.close - buy.close)
        val profit = if (diff < 0)
            "-$%.2f".format(-diff)
        else
            "$%.2f".format(diff)

        val fields: List<String> = listOf(
            symbol.padEnd(8, ' '),
                   buy.date.toString(),
                    "%dd".format(sell.date.diff(buy.date)),
                    profit.padStart(6, ' '),
                    "%.2f".format(sell.getPercentDiff(buy)).padStart(6, ' ') + "%"
        )

        return fields.joinToString("\t")
    }
}