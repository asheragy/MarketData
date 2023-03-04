package org.cerion.marketdata.core.model

import java.time.DayOfWeek
import java.time.LocalDate

data class OHLCVRow(
    val date: LocalDate,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
) {
    init {
        //Error checking
        if (open < low || close < low || open > high || close > high)
            throw RuntimeException("OHLCV range inconsistency ${date}: $open, $high, $low, $close")
    }

    //Typical price
    val tp: Float
        get() = (close + high + low) / 3

    val dow: DayOfWeek
        get() = date.dayOfWeek

    // TODO should return value NOT multiplied by 100
    fun getPercentDiff(old: OHLCVRow): Float {
        if (old.date > date)
            throw RuntimeException("current price is older than input price")

        val diff = close - old.close
        return 100 * (diff / old.close)
    }

    //Money flow volume
    val mfv: Float
        get() {
            var mult = (close - low - (high - close)) / (high - low)
            if (close == low)
                mult = -1f
            if (low == high)
            //divide by zero
                mult = 0f

            return mult * volume
        }
}