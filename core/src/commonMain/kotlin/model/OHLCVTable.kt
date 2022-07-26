package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.toFloatArray
import org.cerion.marketdata.core.platform.KMPDate
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

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

    private fun pricesPerYear(): Int {
        return when (interval) {
            Interval.DAILY -> 252
            Interval.WEEKLY -> 52
            Interval.MONTHLY -> 12
            Interval.QUARTERLY -> 4
            else -> 1
        }
    }
}