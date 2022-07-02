package org.cerion.marketdata.webclients.coingecko

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.webclients.tda.RequestException
import org.json.JSONObject
import java.net.HttpURLConnection

class CoinGecko {
    private val client = OkHttpClient()

    fun getPrices(currencies: List<String>): Map<String, Double> {
        val ids = currencies.joinToString(",")
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/simple/price?ids=$ids&vs_currencies=usd")
            .build()

        val response = client.newCall(request).execute()

        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONObject(body)

        client.connectionPool.evictAll()

        return currencies.associateWith {
            val obj = json[it] as JSONObject
            val value = obj["usd"] as Double
            value
        }
    }

}