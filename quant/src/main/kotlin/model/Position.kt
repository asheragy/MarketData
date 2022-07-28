package model

import org.cerion.marketdata.core.model.OHLCVRow

data class Position(val symbol: String, val buy: OHLCVRow, val percentage: Double) {
    fun close(sell: OHLCVRow) = Trade(symbol, buy, sell, percentage)
}