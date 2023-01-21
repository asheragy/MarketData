
import org.cerion.marketdata.webclients.tda.TDAmeritrade
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Desktop
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI
import java.net.URLDecoder
import java.util.*


class TDAmeritradeTest {
    private val clientId = Secrets.tdClientId
    private val redirectUri = "https://127.0.0.1"
    private val propsFile = "td.props"
    private val api = TDAmeritrade(clientId, redirectUri)

    private var accessToken: String
        get() = getProps()["access"] as String
        set(value) {
            saveSetting("access", value)
        }

    private var refreshToken: String
        get() = getProps()["refresh"] as String
        set(value) {
            saveSetting("refresh", value)
        }

    @Test
    fun login() {
        println(api.authUrlEncoded)
        Desktop.getDesktop().browse(URI(api.authUrlEncoded))
    }

    private val codeFromBrowser = ""

    @Test
    fun auth() {
        val decoded = URLDecoder.decode(codeFromBrowser)

        val response = api.authorize(decoded)
        println(response)

        refreshToken = response.refreshToken!!
        accessToken = response.accessToken
    }

    @Test
    fun refreshAuth() {
        val response = api.refreshAuth(refreshToken)

        println(response)
        accessToken = response.accessToken
    }

    @Test
    fun getPositions() {
        val ps = api.getPositions(accessToken)
        ps.forEach {
            println("${it.symbol}, ${it.quantity}, ${it.pricePerShare}, ${it.totalValue}, isCash=${it.cash}")
        }
    }

    @Test
    fun getQuotes() {
        val quotes = api.getQuotes(listOf("VTSAX", "TSLA"))

        quotes.forEach {
            assertTrue(it.price > 0)
            assertTrue(it.price > it.low52)
            assertTrue(it.price < it.high52)
        }
    }

    private fun saveSetting(key: String, value: String) {
        println("Saving $key = $value")
        val props = getProps()
        props.put(key, value)
        props.store(FileOutputStream(propsFile), null)
    }

    private fun getProps(): Properties {
        val properties = Properties()
        kotlin.runCatching { properties.load(FileInputStream(propsFile)) } // Ignore if file doesn't exist yet
        return properties
    }
}