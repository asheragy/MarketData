package org.cerion.marketdata.core.repository

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.web.FetchInterval


interface IPriceListRepository {
    fun add(list: PriceList)
    fun get(symbol: String, interval: FetchInterval): PriceList?
}

@Deprecated("use new version and rename when this is removed")
interface PriceListRepository {
    fun add(list: PriceList)

    @Deprecated("use PriceList version")
    fun get(symbol: String, interval: Interval): List<OHLCVRow>

    fun getList(symbol: String, interval: Interval): PriceList?

    fun deleteAll()
    
    @Deprecated("")
    operator fun get(symbol: String, interval: Interval, max: Int): PriceList
}
