package org.cerion.marketdata.core.web

import org.cerion.marketdata.core.PriceRow
import java.util.*

// Intervals the APIs allow fetching for
enum class FetchInterval {
    DAILY,
    WEEKLY,
    MONTHLY,
}

interface PriceHistoryDataSource {
    fun getPrices(symbol: String, interval: FetchInterval, start: Date?): List<PriceRow>
}