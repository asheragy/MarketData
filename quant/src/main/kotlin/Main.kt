import data.SectorDataDef
import data.TextDataRepository
import strategy.IndexStrategy
import strategy.RsiStrategy

/* Initial Goals
   strategy.Strategy
   - Output: Misc data points TBD
        - Avg ticks held
        - Max draw down (compare to index)
        - Profit to draw down ratio
        - Biggest win/loss
        - Sharpe ratio
        - Sortino ratio
        - Percent wins
        - Z score
   - Prevent future leak??

   Later
   - Optimization / discovery utils

 */
fun main() {

    val dataSource = TextDataRepository()
    //dataSource.upsert(SectorDataDef())
    val dataSet = dataSource.get(SectorDataDef())
    val strategy = IndexStrategy()

    val result = Backtester.run(dataSet, strategy)
    result.print()
}