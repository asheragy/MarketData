package data

import org.cerion.marketdata.webclients.FetchInterval

class SectorETFDef : DataDef {
    override val symbols: List<String>
        get() = Sectors.map { it.etf }

    override val index: String = "SPY"
    override val interval = FetchInterval.WEEKLY

    // TODO increase to appropriate value
    override val minLength = 10

    // Start date, optional for minDate
}