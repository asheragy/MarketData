package data

import org.cerion.marketdata.webclients.FetchInterval

interface DataDef {
    val symbols: List<String>
    val interval: FetchInterval // TODO can be abstracted

    // Benchmark index
    val index: String?

    // Minimum length of all data sets, error if any lists are shorter
    // TODO should check on read since it might be changed
    val minLength: Int
}

interface DataRepository {
    fun upsert(data: DataDef)

    fun get(data: DataDef): DataSet
}