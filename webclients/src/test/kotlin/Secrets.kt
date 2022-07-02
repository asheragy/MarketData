import java.io.File

object Secrets {

    private val secrets: Map<String, String> by lazy {
        val result = mutableMapOf<String, String>()
        File("secrets").forEachLine {
            val keyval = it.split("=")
            result[keyval[0]] = keyval[1]
        }

        result
    }

    val tiingoApiKey: String = secrets["TIINGO_API_KEY"]!!

    // May not actually be a secret
    val tdClientId: String = secrets["TD_CLIENT_ID"]!!
}