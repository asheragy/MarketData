package org.cerion.marketdata.core.series

data class BandValue(val upper: Float, val lower: Float, val value: Float) {
    val width: Float
        get() = upper - lower

    val mid: Float
        get() = (lower + upper) / 2

    val bandwidth: Float
        get() = width / mid * 100

    val percent: Float
        get() = (value - lower) / width
}

class BandSeries(val upper: FloatSeries, val lower: FloatSeries, private val values: FloatSeries) : Series<BandValue> {

    init {
        require(upper.size == lower.size) { "upper and lower series must be the same size" }
        require(upper.size == values.size) { "band and value series must be the same size" }
    }

    override val size = upper.size

    override fun get(i: Int): BandValue {
        return BandValue(upper(i), lower(i), values[i])
    }

    fun lower(pos: Int): Float = lower[pos]
    fun upper(pos: Int): Float = upper[pos]
    fun width(pos: Int): Float = get(pos).width
    fun mid(pos: Int): Float = get(pos).mid
    fun bandwidth(pos: Int): Float = get(pos).bandwidth
    fun percent(pos: Int): Float = get(pos).percent

    fun percent() = FloatSeries(this.map { it.percent }.toFloatArray())
}
