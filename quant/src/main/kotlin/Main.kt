import data.SectorDataDef
import data.TextDataRepository
import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.platform.KMPDate
import org.cerion.marketdata.core.web.FetchInterval
import org.cerion.marketdata.webclients.yahoo.YahooFinance
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

/* Initial Goals
   - Read in data, truncate to shortest list

   Strategy
   - Input: Symbols / Interval / Benchmark index or null
   - Output: Misc data points TBD
   - Prevent future leak??

   Later
   - Optimization / discovery utils

 */
fun main() {

    val dataSource = TextDataRepository()

    //dataSource.insert(SectorDataDef())
    val test = dataSource.get(SectorDataDef())
    val date = KMPDate(2000,2,3)
    println(date.toISOString())
}