import data.SectorETFDef
import data.SectorIndexDef
import data.TextDataRepository
import strategy.SectorStrategy

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
    val sectorIndex = dataSource.get(SectorIndexDef(), false)
    val dataSet = dataSource.get(SectorETFDef(), false)

    val strategy = SectorStrategy(sectorIndex)

    val result = Backtester.run(dataSet, strategy)
    result.print()
}