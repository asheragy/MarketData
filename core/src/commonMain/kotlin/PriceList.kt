package org.cerion.marketdata.core

import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.platform.DayOfWeek
import org.cerion.marketdata.core.platform.KMPDate
import org.cerion.marketdata.core.platform.KMPTimeStamp
import kotlin.math.*

class PriceList(symbol: String, list: List<OHLCVRow>) : OHLCVTable(symbol, list) {

    private var logScale = false
    var lastUpdated: KMPTimeStamp? = null

    fun getPrice(index: Int): Price = Price(this, index)

    // Skip first instance
    // the first month of a fund may only have a few days worth of arrays depending on its first trading date, example SPY
    val interval: Interval
        get() {
            if (size > 1) {
                val diff = dates[2].diff(dates[1])

                if (diff > 200)
                    return Interval.YEARLY
                else if (diff > 45)
                    return Interval.QUARTERLY
                else if (diff > 10)
                    return Interval.MONTHLY
                else if (diff > 5)
                    return Interval.WEEKLY
            }

            return Interval.DAILY
        }

    val change: Float
        get() = close[size - 1] - close[size - 2]

    val percentChange: Float
        get() = close.getPercentChange(size - 2)

    //Typical price
    fun tp(pos: Int): Float = (close[pos] + high[pos] + low[pos]) / 3

    //Money flow volume
    fun mfv(pos: Int) : Float {
        var mult = (close[pos] - low[pos] - (high[pos] - close[pos])) / (high[pos] - low[pos])
        if (close[pos] == low[pos])
            mult = -1f
        if (low[pos] == high[pos])
        //divide by zero
            mult = 0f

        return mult * volume[pos]
    }

    //Rate of change
    fun roc(pos: Int, period: Int): Float {
        var x = 0 //If period goes beyond start then set to first element
        if (pos >= period)
            x = pos - period

        return (close[pos] - close[x]) * 100 / close[x]
    }

    fun toLogScale(): PriceList {
        if (logScale)
            return this

        val logPrices = ArrayList<OHLCVRow>()
        for (i in 0 until size) {
            val p = get(i)
            logPrices.add(OHLCVRow(p.date, ln(p.open), ln(p.high), ln(p.low), ln(p.close), ln(p.volume)))
        }

        val result = PriceList(symbol, logPrices)
        result.logScale = true
        return result
    }

    @Deprecated("unnecessary", ReplaceWith("this.symbol == symbol"))
    fun `is`(symbol: String): Boolean {
        return this.symbol == symbol
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

                val t1 = get(i - 1).date.time
                val t2 = p.date.time
                var diff = t2 - t1
                diff /= (1000 * 60 * 60 * 24).toLong()

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

    fun getLast(prev: Int): Price {
        return getPrice(size - 1 - prev)
    }

    fun tr(pos: Int): Float {
        return if (pos > 0) max(high[pos], close[pos - 1]) - min(low[pos], close[pos - 1]) else high[0] - low[0]
    }

    fun slope(period: Int, pos: Int): Float {
        return this.close.slope(period, pos)
    }

    fun averageYearlyGain(): Float {
        val count = (size - 1).toFloat()
        val years = count / pricesPerYear()

        // Simple Return = (Current Price-Purchase Price) / Purchase Price
        // Annual Return = (Simple Return +1) ^ (1 / Years Held)-1

        val simpleReturn = last().getPercentDiff(first())
        val a = (simpleReturn / 100 + 1).toDouble()
        val b = (1 / years).toDouble()

        val annualReturn = a.pow(b) - 1
        return annualReturn.toFloat()
    }

    private fun pricesPerYear(): Int {
        return when (interval) {
            Interval.DAILY -> 252
            Interval.WEEKLY -> 52
            Interval.MONTHLY -> 12
            Interval.QUARTERLY -> 4
            else -> 1
        }
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

