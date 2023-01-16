package org.cerion.marketdata.webclients.yahoo

import okhttp3.OkHttpClient
import okhttp3.Request
import org.cerion.marketdata.core.model.Dividend
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.platform.KMPDate
import org.cerion.marketdata.core.platform.toDate
import org.cerion.marketdata.core.utils.CSVParser
import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.PriceHistoryDataSource
import org.cerion.marketdata.webclients.tda.RequestException
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class YahooFinance private constructor() : PriceHistoryDataSource {
    private val client = OkHttpClient()

    // TODO add interval enum specific to this class for more type safety, the other has values this API does not support

    private var mCookieCrumb: String? = null
    private var mCookie: String? = null

    override fun getPrices(symbol: String, interval: FetchInterval, start: LocalDate?): List<OHLCVRow> {
        if (!setCookieCrumb())
            throw RuntimeException("Failed to get cookie")

        //https://query1.finance.yahoo.com/v7/finance/download/OHI
        // ?period1=1493915553
        // &period2=1496593953
        // &interval=1d
        // &events=history
        // &crumb=TSV3DSdPIjI

        var sURL = "https://query1.finance.yahoo.com/v7/finance/download/$symbol"
        if (start != null)
            sURL += "?period1=" + start.toDate().time / 1000
        else
            sURL += "?period1=-1325635200" // This is the date they use for S&P 500 index at max size

        sURL += "&period2=" + Date().time / 1000

        if (interval === FetchInterval.MONTHLY)
            sURL += "&interval=1mo"
        else if (interval === FetchInterval.WEEKLY)
            sURL += "&interval=1wk"
        else
            sURL += "&interval=1d"

        sURL += "&events=history"
        sURL += "&crumb=" + mCookieCrumb!!

        println(sURL)

        val request = Request.Builder().url(sURL).build()
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
        if (!setCookieCrumb())
            throw RuntimeException("Failed to get cookie")

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

    private fun setCookieCrumb(): Boolean {
        if (mCookieCrumb != null)
            return true

        val request = Request.Builder()
            .url("https://finance.yahoo.com/quote/%5EGSPC/options")
            .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()!!

        val index = body.indexOf("Crumb\":\"")
        if (index > 0) {
            if (DEBUG) {
                val debug = body.substring(index, index + 50)
                println(debug)
            }

            val start = index + 8
            val end = body.indexOf("\"", start)
            if (start < end) {
                mCookieCrumb = body.substring(start, end)
                mCookieCrumb = mCookieCrumb!!.replace("\\u002F", "/")

                // Seems to be different for local vs android, if more than 1 get the last entry
                // If this still fails look into better method, might be difference between http vs https requests
                //val cookieHeaders = response.headers.values("Set-Cookie")
                //mCookie = cookieHeaders[cookieHeaders.size - 1]
                //mCookie = mCookie!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                return true
            }

        }

        return false
    }

    companion object {

        private val mDateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val mDateFormatAlt = SimpleDateFormat("M/dd/yyyy")

        val instance = YahooFinance()

        private const val DEBUG = true

        private fun parseDate(inputDate: String): KMPDate? {
            var date = inputDate
            var result: LocalDate? = null
            date = date.replace("\"", "")
            try {
                result = LocalDate.parse(date)
            } catch (e: Exception) {
                // Try alternate
                /*
                try {
                    result = mDateFormatAlt.parse(date)
                } catch (ee: Exception) {
                    result = null
                    //ee.printStackTrace();
                }
                 */
            }

            return if (result != null)
                KMPDate(result)
            else
                null
        }
    }
}
