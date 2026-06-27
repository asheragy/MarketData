package data

import org.cerion.marketdata.webclients.FetchInterval

class SectorIndexDef : DataDef {
    override val symbols: List<String>
        get() = Sectors.map { it.index }

    override val index: String = "^GSPC"
    override val interval = FetchInterval.WEEKLY

    // TODO increase to appropriate value
    override val minLength = 10

    // Start date, optional for minDate
}
