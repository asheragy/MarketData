package org.cerion.marketdata.core

import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.platform.DayOfWeek
import org.cerion.marketdata.core.platform.KMPDate
import org.cerion.marketdata.core.platform.KMPTimeStamp
import kotlin.math.*

@Deprecated("Use OHLCVTable")
class PriceList(symbol: String, list: List<OHLCVRow>) : OHLCVTable(symbol, list) {
    var lastUpdated: KMPTimeStamp? = null

    val change: Float
        get() = close[size - 1] - close[size - 2]

    val percentChange: Float
        get() = close.getPercentChange(size - 2)

    //Rate of change
    fun roc(pos: Int, period: Int): Float {
        var x = 0 //If period goes beyond start then set to first element
        if (pos >= period)
            x = pos - period

        return (close[pos] - close[x]) * 100 / close[x]
    }

    fun truncate(minStartDate: KMPDate): PriceList {
        val prices = mutableListOf<OHLCVRow>()
        for(p in this) {
            if (p.date >= minStartDate)
                prices.add(OHLCVRow(p.date, p.open, p.high, p.low, p.close, p.volume))
        }

        return PriceList(symbol, prices)
    }

    fun toWeekly(): PriceList {
        if (interval !== Interval.DAILY)
            throw RuntimeException("Interval must be daily")

        val prices = ArrayList<OHLCVRow>()

        var i = 0
        while (i < size - 1) {
            val start = get(i)

            val open = start.open
            var close = start.close
            var high = start.high
            var low = start.low
            var volume = start.volume

            while (i < size - 1) {
                i++
                val p = get(i)

                val t1 = get(i - 1).date
                val t2 = p.date
                val diff = t2.diff(t1)

                // New week
                if (diff > 2)
                    break

                volume += p.volume
                if (p.high > high)
                    high = p.high
                if (p.low < low)
                    low = p.low

                close = p.close
            }

            val p = OHLCVRow(start.date, open, high, low, close, volume)
            prices.add(p)
        }

        return PriceList(symbol, prices)
    }

    fun toMonthly(): PriceList {
        if (interval !== Interval.DAILY)
            throw RuntimeException("Interval must be daily")

        val prices = ArrayList<OHLCVRow>()

        var i = 0
        while (i < size - 1) {
            val start = get(i)

            val open = start.open
            var close = start.close
            var high = start.high
            var low = start.low
            var volume = start.volume

            while (i < size - 1) {
                i++
                val p = get(i)

                if (start.date.month != p.date.month)
                    break

                volume += p.volume
                if (p.high > high)
                    high = p.high
                if (p.low < low)
                    low = p.low

                close = p.close
            }

            val p = OHLCVRow(start.date, open, high, low, close, volume)
            prices.add(p)
        }

        return PriceList(symbol, prices)
    }

    fun toQuarterly(): PriceList {
        if (interval !== Interval.MONTHLY)
            throw RuntimeException("Interval must be monthly")

        val prices = ArrayList<OHLCVRow>()
        var i = size - 1
        while (i >= 2) {
            val p1 = get(i)
            val p2 = get(i - 1)
            val p3 = get(i - 2)

            val p = OHLCVRow(p1.date,
                    p3.open,
                    max(max(p1.high, p2.high), p3.high),
                    min(min(p1.low, p2.low), p3.low),
                    p1.close,
                    p1.volume + p2.volume + p3.volume)

            prices.add(p)
            i -= 3
        }

        return PriceList(symbol, prices)
    }

    fun toYearly(): PriceList {
        if (interval !== Interval.MONTHLY)
            throw RuntimeException("Interval must be monthly")

        val prices = ArrayList<OHLCVRow>()
        var i = size - 1
        while (i >= 11) {
            val start = get(i - 11)

            val open = start.open
            val close = get(i).close
            var high = 0f
            var low = open
            var volume = 0f

            for (j in i - 11..i) {
                val q = get(j)
                volume += q.volume
                if (q.high > high)
                    high = q.high
                if (q.low < low)
                    low = q.low
            }

            val p = OHLCVRow(get(i).date, open, high, low, close, volume)
            prices.add(p)
            i -= 12
        }

        return PriceList(symbol, prices)
    }

    fun slope(period: Int, pos: Int): Float {
        return this.close.slope(period, pos)
    }

    companion object {
        fun generateSeries(days: Int): PriceList {
            val dates = mutableListOf<KMPDate>()
            var date = KMPDate.TODAY

            while(dates.size < days) {
                if (date.dayOfWeek == DayOfWeek.SATURDAY)
                    date = date.add(-1)
                else if (date.dayOfWeek == DayOfWeek.SUNDAY)
                    date = date.add(-2)

                dates.add(date)
                date = date.add(-1)
            }

            dates.reverse()

            var base = 100.0f
            val increase = 0.001f // ~10% increase every <period> days
            val periodLength = 200

            val rows = mutableListOf<OHLCVRow>()
            for(i in 0 until days) {
                val period = (i % periodLength) * (PI / periodLength)
                var curr = base + (base * sin(period)).toFloat()

                // Rotate 3 days up and 2 down
                when(i % 5) {
                    0 -> curr += curr*0.02f
                    2 -> curr += curr*0.02f
                    3 -> curr -= curr*0.03f
                    4 -> curr -= curr*0.03f
                }

                val open = curr - (base / 200)
                val high = curr + (base / 100)
                val low = curr - (base / 100)
                val volume = curr * 1000

                rows.add(OHLCVRow(dates[i], open, high, low, curr, volume))
                base += base * increase
            }

            return PriceList("TESTDATA", rows.reversed())
        }
    }
}

