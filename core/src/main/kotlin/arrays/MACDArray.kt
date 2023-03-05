package org.cerion.marketdata.core.arrays

data class MACD(val value: Float, val signal: Float, val hist: Float)

class MACDArray(size: Int, private val signalPeriod: Int) : FloatArray(size) {

    private val signal: FloatArray by lazy {
        ema(signalPeriod)
    }

    //Signal line
    fun signal(pos: Int) : Float = signal[pos]

    //Histogram
    fun hist(pos: Int) : Float = this[pos] - signal[pos]
}
