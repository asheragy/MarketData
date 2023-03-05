package org.cerion.marketdata.core.arrays


abstract class ValueArray<T> : Iterable<T> {

    abstract val size: Int
    abstract operator fun get(i: Int): T

    val first: T by lazy {
        this[0]
    }

    val last: T by lazy {
        this[size - 1]
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var position = 0
            override fun hasNext() = position < size
            override fun next() = get(position++)
        }
    }

    companion object {
        //Use a lower period value when calculating array elements before that position so all values get set to something
        fun maxPeriod(pos: Int, period: Int): Int {
            return if (pos < period - 1) pos + 1 else period
        }
    }
}
