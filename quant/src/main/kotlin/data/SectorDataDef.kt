package data

import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.webclients.FetchInterval

class SectorDataDef : DataDef {
    override val symbols: List<String>
    // TODO add remaining
        get() = listOf("XLE", "XLK", "XLF")

    override val index: String = "SPY"
    override val interval = FetchInterval.WEEKLY

    // TODO increase to appropriate value
    override val minLength = 10

    // Start date, optional for minDate
}