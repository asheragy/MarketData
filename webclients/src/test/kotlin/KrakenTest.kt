import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.webclients.api.Kraken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KrakenTest {

    @Test
    fun test() {
        val api = Kraken()
        val daily = api.getPrices("ETH", Interval.DAILY)
        assertEquals(720, daily.size)

        val weekly = api.getPrices("ETH", Interval.WEEKLY)
        assertTrue(weekly.size > 500)
    }

}
