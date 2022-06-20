package org.cerion.marketdata.core.web.clients

import org.cerion.marketdata.core.model.Symbol
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Tiingo(private val authKey: String) {

    fun getSymbol(symbol: String): Symbol? {
        val url = "https://api.tiingo.com/tiingo/daily/$symbol"
        val data = getData(url)
        val json = JSONObject(data)

        return Symbol(symbol, json["name"].toString(), json["exchangeCode"].toString())
    }

    // TODO replace with OkHTTP
    private fun getData(targetURL: String): String? {

        val url: URL
        var code = 0

        try {
            //Create connection
            url = URL(targetURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.useCaches = false
            connection.doInput = true

            connection.setRequestProperty("Content-type", "application/json")
            connection.setRequestProperty("Authorization", "Token $authKey")

            //Get Response
            code = connection.responseCode
            val stream = connection.inputStream
            val rd = BufferedReader(InputStreamReader(stream))
            var line: String?
            val response = StringBuffer()
            while (true) {
                line = rd.readLine()
                if (line == null)
                    break

                response.append(line)
                response.append('\r')
            }
            rd.close()
            return response.toString()

        } catch (e: Exception) {
            println("Response code = $code")
            //e.printStackTrace()
            return null

        } finally {

            //connection?.disconnect()
        }
    }
}
