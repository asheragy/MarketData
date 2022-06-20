package org.cerion.marketdata.core

import org.cerion.marketdata.core.platform.KMPDate


data class PriceRow(
        override val date: KMPDate,
        override val open: Float,
        override val high: Float,
        override val low: Float,
        override val close: Float,
        override val volume: Float) : IPrice {

    init {
        //Error checking
        if (open < low || close < low || open > high || close > high)
            throw RuntimeException("Price range inconsistency ${date.toISOString()}: $open, $high, $low, $close")
    }

    @Deprecated("unnecessary", ReplaceWith("date.toISOString()"))
    val formattedDate: String
        get() = date.toISOString()
}