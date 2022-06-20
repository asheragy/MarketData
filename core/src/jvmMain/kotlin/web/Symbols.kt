package org.cerion.marketdata.core.web

import java.util.ArrayList

object Symbols {

    //http://www.nasdaqtrader.com/trader.aspx?id=symboldirdefs
    //ftp://ftp.nasdaqtrader.com/symboldirectory/nasdaqlisted.txt

    val sectors = arrayOf("SPY", "XLY", "XLP", "XLE", "XLF", "XLV", "XLI", "XLB", "XLK", "XLU")

    //Some are listed twice, for each class of stock
    //Don't add the one with the least historical arrays
    fun getSP500List(): List<String> {
        val data = Tools.getURL("https://en.wikipedia.org/wiki/List_of_S%26P_500_companies")
        val arr = data!!.split("</a></td>\r\n<td><a href".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val result = ArrayList<String>()
        for (i in 1 until arr.size) {
            val t = arr[i].split(">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var s = t[t.size - 1]
            s = s.trim { it <= ' ' }
            if (s.isNotEmpty() && !s.contentEquals("GOOG") && !s.contentEquals("DISCK") && !s.contentEquals("NWSA") && !s.contentEquals("FOXA")) {
                result.add(s)
            }
        }
        return result
    }
}
