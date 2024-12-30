import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.webclients.api.Schwab
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SchwabTest {
    private val api = Schwab("")

    @Test
    fun priceHistoryDaily() {
        val result = api.priceHistory("AAPL", Interval.DAILY, 200)
        assertEquals(200, result.size)
    }

    @Test
    fun priceHistoryWeekly() {
        val result = api.priceHistory("AAPL", Interval.WEEKLY, 100)
        assertEquals(100, result.size)
    }

    @Test
    fun priceHistoryMonthly() {
        val result = api.priceHistory("AAPL", Interval.MONTHLY, 50)
        assertEquals(50, result.size)
    }
}