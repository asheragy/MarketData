package model

import org.cerion.marketdata.core.model.OHLCVRow

data class Position(val symbol: String, val buy: OHLCVRow, val shares: Double) {
    fun close(sell: OHLCVRow) = Trade(symbol, buy, sell, shares)
}