import org.cerion.marketdata.webclients.coingecko.CoinGecko
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoinGeckoTest {

    private val client = CoinGecko()

    @Test
    fun simpleQuotes() {
        val prices = client.getSimpleQuotes(listOf("bitcoin", "ethereum", "solana"))
        assertEquals(3, prices.size)

        prices.forEach {
            println(it)
            assertTrue(it.id.isNotEmpty())
            assertTrue(it.price != 0.0)
            assertTrue(it.change24h != 0.0)
        }
    }

    @Test
    fun detailedQuotes() {
        val prices = client.getDetailedQuotes(listOf("bitcoin", "ethereum", "solana", "binancecoin", "litecoin"))
        assertEquals(5, prices.size)

        prices.forEach {
            println(it)
            assertTrue(it.id.isNotEmpty())
            assertTrue(it.price != 0.0)
            assertTrue(it.changeDay != 0.0)
            assertTrue(it.changeHour != 0.0)
            assertTrue(it.changeWeek != 0.0)
            assertTrue(it.changeMonth != 0.0)
        }
    }
}