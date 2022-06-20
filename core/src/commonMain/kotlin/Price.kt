package org.cerion.marketdata.core

import org.cerion.marketdata.core.platform.DayOfWeek
import org.cerion.marketdata.core.platform.KMPDate

class Price(val parent: PriceList, val pos: Int) : IPrice {

    override val date: KMPDate get() = parent.dates[pos]
    override val open: Float get() = parent.open[pos]
    override val close: Float get() = parent.close[pos]
    override val high: Float get() = parent.high[pos]
    override val low: Float get() = parent.low[pos]
    override val volume: Float get() = parent.volume[pos]

    @Deprecated("use date directly", ReplaceWith("date.toISOString()"))
    val formattedDate: String
        get() = date.toISOString()

    val dow: DayOfWeek
        get() = date.dayOfWeek

    //Slope of closing price
    fun slope(period: Int): Float {
        return parent.slope(period, pos)
    }

    //Typical price
    fun tp(): Float = parent.tp(pos)
    fun change(prev: Price): Float = getPercentDiff(prev)

    fun getPercentDiff(old: Price): Float {
        if (old.date > date)
            throw RuntimeException("current price is older than input price")

        val diff = close - old.close
        return 100 * (diff / old.close)
    }

    companion object {
        @Deprecated("removing", ReplaceWith("String.format(\"%.2f\", value)"))
        fun getDecimal(value: Float): String {
            return value.toString() //String.format("%.2f", value)
        }
    }
}
