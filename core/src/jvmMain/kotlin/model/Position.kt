package org.cerion.marketdata.core.model

// TODO see if this can be removed from app then do it here, same with DividendHistory
interface Position {
    val symbol: String
    val quantity: Double
    val pricePerShare: Double
    val totalValue: Double
    val cash: Boolean
}
