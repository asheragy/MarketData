package org.cerion.marketdata.webclients.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.PriceHistoryDataSource
import org.cerion.marketdata.webclients.tda.RequestException
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

class Kraken : PriceHistoryDataSource {
    private val client = OkHttpClient()
    private val UTC = ZoneId.of("UTC")

    override fun getPrices(symbol: String, interval: FetchInterval, start: LocalDate?): List<OHLCVRow> {
        return getPrices(symbol, interval)
    }

    fun getPrices(asset: String, fetchInterval: FetchInterval): List<OHLCVRow> {
        val interval = when (fetchInterval) {
            FetchInterval.DAILY -> "1440"
            FetchInterval.WEEKLY -> "10080"
            else -> throw Exception("Invalid interval $fetchInterval")
        }

        val pair = "X${asset}ZUSD"
        val request = Request.Builder()
            .url("https://api.kraken.com/0/public/OHLC?pair=${pair}&interval=${interval}")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        val json = JSONObject(body)
        val result = json.getJSONObject("result")
        val arr = result.getJSONArray(pair)
        val rows = mutableListOf<OHLCVRow>()

        for (i in 0 until arr.length()) {
            val x = arr.get(i) as JSONArray
            // [int <time>, string <open>, string <high>, string <low>, string <close>, string <vwap>, string <volume>, int <count>]
            val date = Date(x.getLong(0) * 1000)
            val utc = ZonedDateTime.ofInstant(date.toInstant(), UTC).toLocalDate()
            rows.add(OHLCVRow(
                utc,
                x.getString(1).toFloat(),
                x.getString(2).toFloat(),
                x.getString(3).toFloat(),
                x.getString(4).toFloat(),
                x.getString(6).toFloat()))
        }

        return rows
    }
}
