package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.platform.KMPDate

data class OHLCVRow(
    val date: KMPDate,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
) {
    init {
        //Error checking
        if (open < low || close < low || open > high || close > high)
            throw RuntimeException("OHLCV range inconsistency ${date.toISOString()}: $open, $high, $low, $close")
    }
}