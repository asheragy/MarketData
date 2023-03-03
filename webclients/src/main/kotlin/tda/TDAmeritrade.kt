package org.cerion.marketdata.webclients.tda


import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.webclients.OAuthResponse
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection


data class TDPosition(val data: JSONObject) {
    private val instrument = data["instrument"] as JSONObject
    private val averagePrice = data["averagePrice"] as Double

    val symbol: String = instrument["symbol"] as String
    val quantity: Double = data["longQuantity"] as Double
    val totalValue: Double = data["marketValue"] as Double

    val cash
        get() = averagePrice == 1.0

    val pricePerShare: Double
        get() = totalValue / quantity
}

data class Quote(
    val symbol: String,
    val price: Double,
    val description: String,
    val change: Double,
    val high52: Double,
    val low52: Double)

private const val HOST = "https://api.tdameritrade.com"

class TDAmeritrade(val consumerKey: String, redirectUri: String) {
    private val client = OkHttpClient()
    private val auth = TDAmeritradeAuth(consumerKey, redirectUri)

    // OAuth
    val authUrlEncoded = auth.authUrlEncoded
    fun authorize(code: String): OAuthResponse = auth.authorize(code)
    fun refreshAuth(refreshToken: String): OAuthResponse = auth.refreshAuth(refreshToken)

    fun getPositions(auth: String): List<TDPosition> {
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

    fun getQuotes(symbols: List<String>): List<Quote> {
        val params = symbols.joinToString(",")
        val request = Request.Builder()
            .url("$HOST/v1/marketdata/quotes?apikey=$consumerKey&symbol=$params")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONObject(body)

        return symbols.map { symbol ->
            val quote = json[symbol] as JSONObject
            val type = quote["assetType"]
            val desc = quote["description"] as String
            val high52 = quote["52WkHigh"] as Double
            val low52 = quote["52WkLow"] as Double

            val price = when(type) {
                "ETF", "EQUITY" -> quote["lastPrice"] as Double
                "MUTUAL_FUND" -> quote["closePrice"] as Double
                else -> TODO("Unexpected assetType")
            }

            val change = when(type) {
                "ETF", "EQUITY" -> quote["regularMarketPercentChangeInDouble"] as Double
                "MUTUAL_FUND" -> quote["netPercentChangeInDouble"] as Double
                else -> TODO("Unexpected assetType")
            }

            Quote(symbol, price, desc, change, high52, low52)
        }
    }
}