package org.cerion.marketdata.core.repository

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.web.FetchInterval


interface PriceListRepository {
    fun add(list: PriceList)
    fun get(symbol: String, interval: FetchInterval): PriceList?
}
