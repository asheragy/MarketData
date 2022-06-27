package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.platform.KMPDate

class DividendHistory(dividendList: List<Dividend>, startDate: KMPDate) {
    val list: MutableList<Dividend> = mutableListOf()

    init {
        list.addAll(dividendList.sortedByDescending { it.date })
    }

    val totalDividends: Double by lazy {
        var total = 0.0
        for (d in list) {
            if (d.date > startDate) {
                total += d.dividend
            } else
                break
        }

        total
    }

    /**
     * Gets the date of last dividend
     * @return Date, or null if no dividends
     */
    var lastDividendDate: KMPDate? = if(list.isNotEmpty()) list[0].date else null

    /**
     * Amount of last dividend in dollars issued per share
     * @return last dividend value
     */
    var lastDividend: Double? = if(list.isNotEmpty()) list[0].dividend.toDouble() else null

    /**
     * Gets the estimated date of the next dividend based on last 2 dividends
     * @return estimated Date
     */
    val nextDividendEstimate: KMPDate? by lazy {
        var result: KMPDate? = null

        // Get 2nd to last dividend
        if (list.size > 1) {
            val last = list[0]
            val secondToLast = list[1]

            val daysDiff = last.date.diff(secondToLast.date)
            result = last.date.add(daysDiff)
        }

        result
    }
}
