package org.cerion.marketdata.webclients.coingecko

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.webclients.tda.RequestException
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection

class CoinGecko {
    private val client = OkHttpClient()

    fun list() {
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/coins/list")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONArray(body)
        println(json.length())
    }

    fun getSimpleQuotes(currencies: List<String>): List<SimpleQuote> {
        val ids = currencies.joinToString(",")
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/simple/price?ids=$ids&vs_currencies=usd&include_24hr_change=true")
            .build()

        val response = client.newCall(request).execute()

        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONObject(body)

        client.connectionPool.evictAll()

        return currencies.associateWith {
            val obj = json[it] as JSONObject
            val price = obj["usd"] as Number
            val change = obj["usd_24h_change"] as Double
            SimpleQuote(it, price.toDouble(), change)
        }.values.toList()
    }

    fun getDetailedQuotes(currencies: List<String>): List<DetailedQuote> {
        val ids = currencies.joinToString(",")
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/coins/markets?ids=$ids&vs_currency=usd&price_change_percentage=1h,24h,7d")
            .build()

        val response = client.newCall(request).execute()

        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONArray(body)

        client.connectionPool.evictAll()

        // For some reason json.map is not working on android
        val result = mutableListOf<DetailedQuote>()

        for(i in 0 until json.length()) {
            val it = json.get(i) as JSONObject

            val id = it["id"] as String
            val price = it["current_price"] as Number
            val changeHour = it["price_change_percentage_1h_in_currency"] as Double
            val changeDay = it["price_change_percentage_24h_in_currency"] as Double
            val changeWeek = it["price_change_percentage_7d_in_currency"] as Double
            val quote = DetailedQuote(id, price.toDouble(), changeHour, changeDay, changeWeek)
            result.add(quote)
        }

        return result
    }

    data class SimpleQuote(val id: String, val price: Double, val change24h: Double)

    data class DetailedQuote(val id: String,
                             val price: Double,
                             val changeHour: Double,
                             val changeDay: Double,
                             val changeWeek: Double)
}

