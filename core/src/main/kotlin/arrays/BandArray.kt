package org.cerion.marketdata.core.arrays

class BandArray(private val source: FloatArray, val upper: FloatArray, val lower: FloatArray) : ValueArray() {

    override val size = source.size

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