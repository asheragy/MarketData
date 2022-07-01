package org.cerion.marketdata.webclients

import org.cerion.marketdata.core.model.OHLCVRow
import java.time.LocalDate

// Intervals the APIs allow fetching for
enum class FetchInterval {
    DAILY,
    WEEKLY,
    MONTHLY,
}

interface PriceHistoryDataSource {
    fun getPrices(symbol: String, interval: FetchInterval, start: LocalDate?): List<OHLCVRow>
}