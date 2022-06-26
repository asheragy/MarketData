package org.cerion.marketdata.core

import org.cerion.marketdata.core.platform.KMPDate

@Deprecated("Use OHLCVRow")
interface IPrice {
    val date: KMPDate
    val open: Float
    val close: Float
    val high: Float
    val low: Float
    val volume: Float
}