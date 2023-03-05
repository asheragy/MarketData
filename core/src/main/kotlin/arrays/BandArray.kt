package org.cerion.marketdata.core.arrays

data class BandValue(val upper: Float, val lower: Float, val mid: Float, val bandwidth: Float, val percent: Float)

class BandArray(private val source: FloatArray, val upper: FloatArray, val lower: FloatArray) : ValueArray<BandValue>() {

    override val size = source.size

    override fun get(i: Int): BandValue {
        return BandValue(upper(i), lower(i), mid(i), bandwidth(i), percent(i))
    }

    fun mid(pos: Int): Float = (lower[pos] + upper[pos]) / 2
    fun lower(pos: Int): Float = lower[pos]
    fun upper(pos: Int): Float = upper[pos]

    fun bandwidth(pos: Int): Float {
        //(Upper Band - Lower Band)/Middle Band
        return (upper(pos) - lower(pos)) / mid(pos) * 100
    }

    fun percent(pos: Int): Float {
        //%B = (Price - Lower Band)/(Upper Band - Lower Band)
        return (source[pos] - lower(pos)) / (upper(pos) - lower(pos))
    }
}