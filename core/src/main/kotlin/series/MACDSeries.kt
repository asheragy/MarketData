package org.cerion.marketdata.core.series

data class MACD(val value: Float, val signal: Float, val hist: Float)

// TODO stop inheriting from single array
class MACDSeries(size: Int, private val signalPeriod: Int) : FloatSeries(size) {

    private val signal: FloatSeries by lazy {
        ema(signalPeriod)
    }

    //Signal line
    fun signal(pos: Int) : Float = signal[pos]

    //Histogram
    fun hist(pos: Int) : Float = this[pos] - signal[pos]
}
