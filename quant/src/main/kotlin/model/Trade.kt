package model

import org.cerion.marketdata.core.model.OHLCVRow
import utils.diff

data class Trade(val symbol: String, val buy: OHLCVRow, val sell: OHLCVRow, val shares: Double) {
    val value: Double
        get() {
            return shares * sell.close
        }

    override fun toString(): String {
        return symbol + "\t" + buy.date + "\t" + sell.date.diff(buy.date) + "d\t" + sell.getPercentDiff(buy)
    }
}