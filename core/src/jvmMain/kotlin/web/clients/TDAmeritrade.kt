package org.cerion.marketdata.core.web.clients


import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.Position
import org.cerion.marketdata.core.web.OAuthResponse
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection


private class TDPosition(val data: JSONObject) : Position {
    private val instrument = data["instrument"] as JSONObject
    private val averagePrice = data["averagePrice"] as Double

    override val symbol: String = instrument["symbol"] as String
    override val quantity: Double = data["longQuantity"] as Double
    override val totalValue: Double = data["marketValue"] as Double

    override val cash
        get() = averagePrice == 1.0

    override val pricePerShare: Double
        get() = totalValue / quantity
}

private const val HOST = "https://api.tdameritrade.com"

class TDAmeritrade(consumerKey: String, redirectUri: String) {
    private val client = OkHttpClient()
    private val auth = TDAmeritradeAuth(consumerKey, redirectUri)

    // OAuth
    val authUrlEncoded = auth.authUrlEncoded
    fun authorize(code: String): OAuthResponse = auth.authorize(code)
    fun refreshAuth(refreshToken: String): OAuthResponse = auth.refreshAuth(refreshToken)

    fun getPositions(auth: String): List<Position> {
        val request = Request.Builder()
                .header("Authorization", "Bearer $auth")
                .url("$HOST/v1/accounts?fields=positions")
                .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val accounts = JSONArray(body)[0] as JSONObject
        val securitiesAccount = accounts["securitiesAccount"] as JSONObject
        val positions = securitiesAccount["positions"] as JSONArray

        val result = mutableListOf<TDPosition>()

        for(i in 0 until positions.length()) {
            val p = positions[i] as JSONObject
            val position = TDPosition(p)
            result.add(position)
        }

        return result
    }
}