package org.cerion.marketdata.core.repository

import org.cerion.marketdata.core.model.Dividend

interface DividendRepository {
    fun get(symbol: String): List<Dividend>
    fun add(symbol: String, list: List<Dividend>)
    fun deleteAll()
}
