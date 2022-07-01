package data

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.webclients.FetchInterval

interface DataDef {
    val symbols: List<String>
    val interval: FetchInterval // TODO can be abstracted

    // Benchmark index
    val index: String?

    // Minimum length of all data sets, error if any lists are shorter
    val minLength: Int
}

data class DataSet(val lists: List<PriceList>, val index: PriceList? = null)

interface DataRepository {
    fun upsert(data: DataDef)

    // TODO return DataSet object
    fun get(data: DataDef): DataSet
}