import org.cerion.marketdata.webclients.coingecko.CoinGecko
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoinGeckoTest {

    private val client = CoinGecko()

    @Test
    fun simplePrice() {
        val prices = client.getPrices(listOf("bitcoin", "ethereum", "solana"))
        assertEquals(3, prices.size)

        prices.forEach {
            println(it)
            assertTrue(it.id.isNotEmpty())
            assertTrue(it.price != 0.0)
            assertTrue(it.change24h != 0.0)
        }
    }
}