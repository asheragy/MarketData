import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.webclients.api.Schwab
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

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

    @Test
    fun dateRange() {
        val start = LocalDate.of(2024, 1, 3)
        val end = LocalDate.of(2024, 7, 3)
        val result = api.priceHistory("AAPL", Interval.DAILY, start, end)

        assertEquals(126, result.size)
        assertEquals(start, result.first().date)
        assertEquals(end, result.last().date)
    }
}