package org.cerion.marketdata.webclients.api

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.webclients.tda.RequestException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class Schwab(private val token: String) {
    private val client = OkHttpClient()

    fun priceHistory(symbol: String, interval: Interval, count: Int): List<OHLCVRow> {
        val daysBack = when (interval) {
            Interval.DAILY -> (count / 250.0) * 365 + 5
            Interval.WEEKLY -> (count / 52.0) * 365 + 14
            Interval.MONTHLY -> (count / 12.0) * 365 + 35
            else -> throw Exception("Invalid interval $interval")
        }

        val startDate = LocalDate.now().minusDays(daysBack.toLong())
        val list = priceHistory(symbol, interval, startDate, null)
        return list.takeLast(count)
    }

    fun priceHistory(symbol: String, interval: Interval, startDate: LocalDate, endDate: LocalDate?): List<OHLCVRow> {
        val start = startDate.atStartOfDay(ZoneId.of("America/New_York")).toEpochSecond() * 1000

        val frequency = when (interval) {
            Interval.DAILY -> "daily"
            Interval.WEEKLY -> "weekly"
            Interval.MONTHLY -> "monthly"
            else -> throw Exception("Invalid interval $interval")
        }

        var url = "https://api.schwabapi.com/marketdata/v1/pricehistory?symbol=$symbol&periodType=year&frequencyType=$frequency&startDate=$start"
        if (endDate != null)
            url += "&endDate=" + endDate.atStartOfDay(ZoneId.of("America/New_York")).toEpochSecond() * 1000

        val request = Request.Builder()
            .header("Authorization", "Bearer $token")
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val json = JSONObject(response.body?.string())
        val rows = json.getJSONArray("candles")
        val result = mutableListOf<OHLCVRow>()

        for (i in 0 until rows.length()) {
            val r = rows[i] as JSONObject
            val date = Instant.ofEpochMilli(r.getLong("datetime")).atZone(ZoneOffset.UTC).toLocalDate()

            result.add(
                OHLCVRow(
                    date,
                    r.getFloat("open"),
                    r.getFloat("high"),
                    r.getFloat("low"),
                    r.getFloat("close"),
                    r.getFloat("volume")
                )
            )
        }

        return result
    }
}