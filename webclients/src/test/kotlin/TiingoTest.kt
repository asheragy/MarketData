import org.cerion.marketdata.webclients.tiingo.Tiingo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TiingoTest {

    private val api = Tiingo(Secrets.tiingoApiKey)

    @Test
    fun symbol() {
        val symbol = api.getSymbol("OHI")!!
        assertEquals("NYSE", symbol.exchange)
    }
}