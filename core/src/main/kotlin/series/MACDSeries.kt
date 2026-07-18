package org.cerion.marketdata.core.series

data class MACD(val value: Float, val signal: Float, val hist: Float)

class MACDSeries(val macd: FloatSeries, val signal: FloatSeries) : Series<MACD> {

    init {
        require(macd.size == signal.size) { "macd and signal series must be the same size" }
    }

    override val size: Int = macd.size

    val hist: FloatSeries by lazy {
        val result = FloatSeries(size)
        for (i in 0 until size)
            result[i] = macd[i] - signal[i]

        result
    }

    override fun get(i: Int): MACD {
        return MACD(macd[i], signal[i], hist[i])
    }

    fun value(pos: Int): Float = macd[pos]

    fun signal(pos: Int): Float = signal[pos]

    fun hist(pos: Int): Float = hist[pos]
}
