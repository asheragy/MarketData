import data.SectorDataDef
import data.TextDataRepository
import org.cerion.marketdata.core.platform.KMPDate

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
    //dataSource.upsert(SectorDataDef())
    val test = dataSource.get(SectorDataDef())

    println(test.lists)

}