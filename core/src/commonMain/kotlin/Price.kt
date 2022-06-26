package org.cerion.marketdata.core

import org.cerion.marketdata.core.platform.DayOfWeek
import org.cerion.marketdata.core.platform.KMPDate

class Price(parent: PriceList, pos: Int) {

    val date = parent.dates[pos]
    val open = parent.open[pos]
    val close = parent.close[pos]
    val high = parent.high[pos]
    val low = parent.low[pos]
    val volume = parent.volume[pos]

    @Deprecated("use date directly", ReplaceWith("date.toISOString()"))
    val formattedDate: String
        get() = date.toISOString()

    val dow: DayOfWeek
        get() = date.dayOfWeek

    //Slope of closing price
    //fun slope(period: Int): Float {
    //    return parent.slope(period, pos)
    //}

    //Typical price
    //fun tp(): Float = parent.tp(pos)
    fun change(prev: Price): Float = getPercentDiff(prev)

    fun getPercentDiff(old: Price): Float {
        if (old.date > date)
            throw RuntimeException("current price is older than input price")

        val diff = close - old.close
        return 100 * (diff / old.close)
    }
}
