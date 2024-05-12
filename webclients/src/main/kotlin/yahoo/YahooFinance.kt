package org.cerion.marketdata.webclients.yahoo

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.Dividend
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.utils.CSVParser
import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.PriceHistoryDataSource
import org.cerion.marketdata.webclients.tda.RequestException
import utils.toDate
import java.net.HttpURLConnection
import java.time.LocalDate
import java.util.*

class YahooFinance private constructor() : PriceHistoryDataSource {
    private val client = OkHttpClient()

    override fun getPrices(symbol: String, interval: FetchInterval, start: LocalDate?): List<OHLCVRow> {
        //https://query1.finance.yahoo.com/v7/finance/download/OHI
        // ?period1=1493915553
        // &period2=1496593953
        // &interval=1d
        // &events=history
        // &crumb=TSV3DSdPIjI

        var url = "https://query1.finance.yahoo.com/v7/finance/download/$symbol"
        if (start != null)
            url += "?period1=" + start.toDate().time / 1000
        else
            url += "?period1=-1325635200" // This is the date they use for S&P 500 index at max size

        url += "&period2=" + Date().time / 1000
        url += "&interval=" + when(interval) {
            FetchInterval.DAILY -> "1d"
            FetchInterval.WEEKLY -> "1wk"
            FetchInterval.MONTHLY -> "1mo"
        }

        url += "&events=history"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()!!
        if (DEBUG) {
            println("Response size = " + body.length)
            if (body.length < 1000)
                println(body)
        }

        return CSVParser.getPricesFromTable(body)
    }

    fun getDividends(symbol: String): List<Dividend> {
        TODO("Convert to okhttp")
        /*
        var sURL = "https://query1.finance.yahoo.com/v7/finance/download/$symbol"
        sURL += "?period1=946684800" // Jan 1, 2000
        sURL += "&period2=" + Date().time / 1000
        sURL += "&interval=1wk&events=div"
        sURL += "&crumb=" + mCookieCrumb!!

        val result = ArrayList<Dividend>()
        val res = Tools.getURL(sURL, mCookie)

        val sData = res.result
        val lines = sData.split("\\r\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in 1 until lines.size) {
            val line = lines[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val div = Dividend(parseDate(line[0])!!, java.lang.Float.parseFloat(line[1]))
            result.add(0, div)
        }

        return result
         */
    }

    companion object {
        val instance = YahooFinance()
        private const val DEBUG = false
    }
}
