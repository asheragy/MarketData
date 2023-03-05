package org.cerion.marketdata.core.arrays

/** Pair of related lines with opposing directional meaning
 * @param positive Higher value or positive directional value
 * @param negative Lower value or negative directional value
 */

data class PairValue(val positive: Float, val negative: Float) {
    val diff = positive - negative
}

// Refer to these as positive and negative, BandArray will use upper/lower so the differences are more clear
// Higher value OR positive direction value (DI+, VI+, Aroon Up)
// Lower value OR negative direction value  (DI-, VI-, Aroon Down)
class PairArray(val positive: FloatArray, val negative: FloatArray) : ValueArray<PairValue>() {

    override val size: Int = positive.size
    override fun get(i: Int) = PairValue(pos(i), neg(i))

    /*
     * Get upper or positive direction value
     */
    fun pos(pos: Int): Float = positive[pos]

    /*
	 * Get lower or negative direction value
	 */
    fun neg(pos: Int): Float = negative[pos]

    fun diff(pos: Int): Float = positive[pos] - negative[pos]
}
