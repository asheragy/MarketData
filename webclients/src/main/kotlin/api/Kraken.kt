package org.cerion.marketdata.webclients.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.webclients.tda.RequestException
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class Kraken {
    private val client = OkHttpClient()
    private val UTC = ZoneId.of("UTC")

    fun getPrices(asset: String, interval: Interval): List<OHLCVRow> {
        val intervalDays = when (interval) {
            Interval.DAILY -> "1440"
            Interval.WEEKLY -> "10080"
            else -> throw Exception("Invalid interval $interval")
        }

        val pair = "X${asset}ZUSD"
        val request = Request.Builder()
            .url("https://api.kraken.com/0/public/OHLC?pair=${pair}&interval=${intervalDays}")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONObject(body)
        val result = json.getJSONObject("result")
        val arr = result.getJSONArray(pair)
        // [int <time>, string <open>, string <high>, string <low>, string <close>, string <vwap>, string <volume>, int <count>]
        return arr.map { x -> x as JSONArray }.map { x ->
            val date = Date(x.getLong(0) * 1000)
            val utc = ZonedDateTime.ofInstant(date.toInstant(), UTC).toLocalDate()
            OHLCVRow(
                utc,
                x.getString(1).toFloat(),
                x.getString(2).toFloat(),
                x.getString(3).toFloat(),
                x.getString(4).toFloat(),
                x.getString(6).toFloat())
        }
    }
}
