import data.SectorDataDef
import data.TextDataRepository
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
      - Cannot look at prices > current index
      - Cannot buy/sell other than current day

   Later
   - Optimization / discovery utils

 */
fun main() {

    val dataSource = TextDataRepository()
    //dataSource.upsert(SectorDataDef())
    val dataSet = dataSource.get(SectorDataDef())
    val strategy = RsiStrategy()

    val result = Backtester.run(dataSet, strategy)
    result.print()
}