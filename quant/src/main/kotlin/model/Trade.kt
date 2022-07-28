package model

import org.cerion.marketdata.core.model.OHLCVRow

data class Trade(val symbol: String, val buy: OHLCVRow, val sell: OHLCVRow, val percentage: Double) {
    val profit: Double
        get() = sell.getPercentDiff(buy) / 100.0

    override fun toString(): String {
        return symbol + "\t" + buy.date + "\t" + sell.date.diff(buy.date) + "d\t" + sell.getPercentDiff(buy)
    }
}