import data.SectorDataDef
import data.TextDataRepository
import org.cerion.marketdata.core.platform.KMPDate

/* Initial Goals
   - Update mechanism that is tied to reading (update only symbols in data set X)
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