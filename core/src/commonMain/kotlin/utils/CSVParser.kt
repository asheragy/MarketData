package org.cerion.marketdata.core.utils

import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.platform.KMPDate

object CSVParser {

    /***
     * Gets PriceList from csv formatted file that API would return
     * @param tableData file contents as string
     * @return PriceList
     */
    fun getPricesFromTable(tableData: String): MutableList<OHLCVRow> {
        val lines = tableData.split("\\r\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        //if (DEBUG)
        //    println("Table lines = " + lines.size)

        val prices = ArrayList<OHLCVRow>()
        for (i in 1 until lines.size) {
            if (!lines[i].contains("null"))
                prices.add(parseLine(lines[i]))
            else
                println("Ignoring line " + lines[i]) // new API issue some rows have all null values
        }

        return prices
    }

    fun parseLine(sLine: String): OHLCVRow {
        val fields = sLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (fields.size == 7) {
            //TODO fix this for S&P large numbers
            try
            //Fails on S&P index since too large, just ignore it
            {
                var open = fields[1].toFloat()
                var high = fields[2].toFloat()
                var low = fields[3].toFloat()
                val close = fields[4].toFloat()
                val adjClose = fields[5].toFloat()

                // Normalize open/high/low
                if (adjClose != close) {
                    if (close == open)
                        open = adjClose
                    else
                        open = adjClose * open / close

                    if (close == high)
                    //Fix float rounding issues to prevent close > high when they are the same
                        high = adjClose
                    else
                        high = adjClose * high / close

                    if (close == low)
                        low = adjClose
                    else
                        low = adjClose * low / close
                }

                // Correcting bad data
                if (open < low) {
                    println("Correcting bad [open] data on " + fields[0])
                    open = (high + low) / 2
                }

                var volume = fields[6].toLong()
                volume /= 1000
                val date = KMPDate.parse(fields[0])

                return OHLCVRow(date, open, high, low, adjClose, volume.toFloat())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        throw Exception("Unexpected price line")
    }
}