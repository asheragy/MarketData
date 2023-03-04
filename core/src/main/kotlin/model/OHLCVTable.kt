package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.toFloatArray
import org.cerion.marketdata.core.platform.DayOfWeek
import org.cerion.marketdata.core.platform.KMPDate
import kotlin.math.*

open class OHLCVTable(
    val symbol: String,
    rows: List<OHLCVRow>,
    delegate: ArrayList<OHLCVRow> = ArrayList()
) : List<OHLCVRow> by delegate {

    // TODO remove and make property of charts only
    private var logScale = false

    // TODO check usages of these, might be better to replace with price in some calculations
    val dates: Array<KMPDate> by lazy { map { it.date }.toTypedArray() }
    val open: FloatArray by lazy { map { it.open }.toFloatArray() }
    val high: FloatArray by lazy { map { it.high }.toFloatArray() }
    val low: FloatArray by lazy { map { it.low }.toFloatArray() }
    val close: FloatArray by lazy { map { it.close }.toFloatArray() }
    val volume: FloatArray by lazy { map { it.volume }.toFloatArray() }

    init {
        val sortedList = rows.sortedBy { it.date }
        delegate.addAll(sortedList)
    }

    val interval: Interval
        get() {
            // Skip first instance
            // the first month of a fund may only have a few days worth of arrays depending on its first trading date, example SPY
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

    // True range
    fun tr(pos: Int): Float {
        return if (pos > 0) max(high[pos], close[pos - 1]) - min(low[pos], close[pos - 1]) else high[0] - low[0]
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

    fun toLogScale(): OHLCVTable {
        if (logScale)
            return this

        val logPrices = ArrayList<OHLCVRow>()
        for (i in 0 until size) {
            val p = get(i)
            logPrices.add(OHLCVRow(p.date, ln(p.open), ln(p.high), ln(p.low), ln(p.close), ln(p.volume)))
        }

        val result = OHLCVTable(symbol, logPrices)
        result.logScale = true
        return result
    }

    /**
     * Verify data set is exactly the same start/end/interval as another
     */
    fun equalRange(other: OHLCVTable): Boolean {
        if (size != other.size || interval != other.interval)
            return false

        if (dates.first() != other.dates.first() || dates.last() != other.dates.last())
            return false

        return true
    }

    fun truncate(startDate: KMPDate?, endDate: KMPDate?): OHLCVTable {
        val startIndex = if(startDate == null) 0 else dates.indexOf(startDate)
        val endIndex = if(endDate == null) size - 1 else dates.indexOf(endDate)

        return OHLCVTable(symbol, subList(startIndex, endIndex + 1))
    }

    fun toWeekly(): OHLCVTable {
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

        return OHLCVTable(symbol, prices)
    }

    fun toMonthly(): OHLCVTable {
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

        return OHLCVTable(symbol, prices)
    }

    fun toQuarterly(): OHLCVTable {
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

        return OHLCVTable(symbol, prices)
    }

    fun toYearly(): OHLCVTable {
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

        return OHLCVTable(symbol, prices)
    }

    fun beta(index: OHLCVTable, period: Int): FloatArray {
        if (size != index.size)
            throw IllegalArgumentException("Must be same size")

        val stockChange = close.percentChange
        val indexChange = index.close.percentChange

        val covar = stockChange.covariance(indexChange, period)
        val variance = indexChange.variance(period)
        val result = FloatArray(size)

        for(i in 1 until size)
            result[i] = covar[i] / variance[i]

        return result
    }

    // TODO different with crypto
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
        fun generateSeries(days: Int): OHLCVTable {
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

            return OHLCVTable("TESTDATA", rows.reversed())
        }
    }
}