
import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.api.Kraken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KrakenTest {

    @Test
    fun test() {
        val api = Kraken()
        val daily = api.getPrices("ETH", FetchInterval.DAILY)
        assertEquals(721, daily.size)

        val weekly = api.getPrices("ETH", FetchInterval.WEEKLY)
        assertTrue(weekly.size > 500)
    }

}
