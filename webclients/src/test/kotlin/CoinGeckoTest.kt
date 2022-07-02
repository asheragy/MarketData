import org.cerion.marketdata.webclients.coingecko.CoinGecko
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoinGeckoTest {

    private val client = CoinGecko()

    @Test
    fun simplePrice() {
        val prices = client.getPrices(listOf("bitcoin", "ethereum", "solana"))
        println(prices)

        assertEquals(3, prices.size)
        assertTrue(prices["bitcoin"]!! > 0)
        assertTrue(prices["ethereum"]!! > 0)
        assertTrue(prices["solana"]!! > 0)
    }
}