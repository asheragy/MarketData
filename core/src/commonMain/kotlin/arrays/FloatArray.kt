package org.cerion.marketdata.core.arrays

import org.cerion.marketdata.core.overlays.BollingerBands
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.overlays.LinearRegressionLine
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.math.ln
import kotlin.math.sqrt

open class FloatArray(private val mVal: kotlin.FloatArray) : ValueArray() {

    constructor(length: Int) : this(kotlin.FloatArray(length))

    override val size: Int = mVal.size

    val first: Float by lazy {
        this[0]
    }

    val last: Float by lazy {
        this[size - 1]
    }

    operator fun set(i: Int, value: Float) {
        mVal[i] = value
    }

    operator fun get(pos: Int): Float = mVal[pos]

    /**
     * Calculates percent difference between each entry
     * @return array of percent change from previous element
     */
    val percentChange: FloatArray
        get() {
            val arr = FloatArray(size)

            for (i in 1 until size)
                arr[i] = (this[i] - this[i - 1]) / this[i - 1]

            return arr
        }

    /**
     * Find the highest value in the range [start,end]
     * @param start first position to start looking
     * @param end last position to look
     * @return the maximum value in the range
     */
    fun max(start: Int, end: Int): Float = this[maxPos(start, end)]

    /**
     * Find the lowest value in the range [start,end]
     * @param start first position to start looking
     * @param end last position to look
     * @return the minimum value in the range
     */
    fun min(start: Int, end: Int): Float = this[minPos(start, end)]

    /**
     * Returns position of max value between [start,end]
     */
    fun maxPos(start: Int, end: Int): Int {
        var max = start
        for (i in start + 1..end) {
            if (get(i) > get(max))
                max = i
        }
        return max
    }

    /**
     * Returns position of max value between [start,end]
     */
    fun minPos(start: Int, end: Int): Int {
        var min = start
        for (i in start + 1..end) {
            if (get(i) < get(min))
                min = i
        }
        return min
    }

    /**
     * Sum of inclusive range
     */
    fun sum(start: Int, end: Int): Float {
        var sum = 0f
        for (i in start..end) {
            sum += get(i)
        }

        return sum
    }

    fun sma(period: Int): FloatArray = SimpleMovingAverage(period).eval(this)
    fun ema(period: Int): FloatArray = ExpMovingAverage(period).eval(this)
    fun bb(period: Int, multiplier: Float): BandArray = BollingerBands(period, multiplier.toDouble()).eval(this)

    /**
     * Converts current array values to log(val)
     * @return FloatArray to log scale
     */
    fun log(): FloatArray {
        val result = FloatArray(size)
        for (i in 0 until size)
            result[i] = ln(get(i).toDouble()).toFloat()

        return result
    }

    //Rate of change
    fun roc(pos: Int, period: Int): Float {
        var x = 0 //If period goes beyond start then set to first element
        if (pos >= period)
            x = pos - period

        return (this[pos] - this[x]) * 100 / this[x]
    }

    /**
     * Standard deviation of period
     * @param period The period to use for the average
     * @return standard deviation of the average in period
     */
    //If the SimpleMovingAverage is available this function can be called directly to avoid re-calculating
    fun std(period: Int, arr_sma: FloatArray = sma(period)): FloatArray {
        val result = FloatArray(size)

        for (i in 1 until size) {
            val count = maxPeriod(i, period)

            val sma = arr_sma[i]
            var total = 0f

            for (j in i - count + 1..i) {
                val diff = this[j] - sma
                total += diff * diff
            }

            result[i] = sqrt((total / count).toDouble()).toFloat()
        }

        return result
    }

    fun slope(period: Int, pos: Int): Float {
        var p = period
        p = maxPeriod(pos, p)
        val ab = getLinearRegressionEquation(pos - p + 1, pos)

        return ab[1]
    }

    fun getPercentChange(index: Int): Float {
        return (this[size - 1] - this[index]) / this[index]
    }

    fun regressionLinePoint(period: Int, pos: Int): Float {
        val count = ValueArray.maxPeriod(pos, period)
        val slope = slope(period, pos)
        val sumY = sum(pos - count + 1, pos)

        return (sumY - slope * count) / count
    }

    fun linearRegressionLine(): FloatArray {
        return LinearRegressionLine().eval(this)
    }

    /**
     * Finds linear regression equation "y = a + bx" for arr with start and end point positions
     * @return pair [a,b]
     */
    fun getLinearRegressionEquation(start: Int, end: Int): kotlin.FloatArray {
        // http://www.statisticshowto.com/how-to-find-a-linear-regression-equation/
        // TODO check this on fake data like a straight line to verify any 1 off issues
        val count = end - start + 1

        if (count == 1)
            return floatArrayOf(this[start], 0f)

        var sumY = 0f
        var sumX = 0f
        var sumXsquared = 0f
        var sumXY = 0f

        var x = 1
        for (i in start..end) {
            val y = this[i]

            sumY += y
            sumX += x.toFloat()
            sumXsquared += (x * x).toFloat()
            sumXY += x * y
            x++
        }

        // Both variables are calculated by n(Sum x^2) - (Sum x)^2
        val divideBy = count * sumXsquared - sumX * sumX

        val a = sumY * sumXsquared - sumX * sumXY
        val b = count * sumXY - sumX * sumY

        return floatArrayOf(a / divideBy, b / divideBy)
    }

    fun correlation(arr: FloatArray): Float {
        val size = kotlin.math.min(size, arr.size).toFloat()

        var sumXX = 0f
        var sumX = 0f
        var sumYY = 0f
        var sumY = 0f
        var sumXY = 0f

        var i = 0
        while (i < size) {
            val x = get(this.size - 1 - i)
            sumX += x
            sumXX += x * x

            val y = arr[arr.size - 1 - i]
            sumY += y
            sumYY += y * y

            sumXY += x * y
            i++
        }

        val Sxx = sumXX - sumX * sumX / size
        val Syy = sumYY - sumY * sumY / size
        val Sxy = sumXY - sumX * sumY / size

        return Sxy / sqrt((Sxx * Syy).toDouble()).toFloat()
    }

    fun variance(period: Int): FloatArray {
        val result = FloatArray(size)
        val sma = sma(period)

        for(i in 1 until size) {
            val start = kotlin.math.max(0, i - period + 1)
            val squares = mVal.toList().subList(start, i + 1).map { it - sma[i] }.map { it * it }
            val N = squares.size - 1

            val sum = squares.sum()
            result[i] = sum / N
        }

        return result
    }

    fun covariance(other: FloatArray, period: Int): FloatArray {
        val result = FloatArray(size)
        val sma1 = sma(period)
        val sma2 = other.sma(period)

        for(i in 1 until size) {
            val start = kotlin.math.max(0, i - period + 1)
            val diff1 = mVal.toList().subList(start, i + 1).map { it - sma1[i] }
            val diff2 = other.mVal.toList().subList(start, i + 1).map { it - sma2[i] }
            val squares = diff1.zip(diff2) { a, b -> a * b }
            val N = squares.size - 1

            val sum = squares.sum()
            result[i] = sum / N
        }

        return result
    }

    //------------ Moved from PriceList, for calculating Beta
    // Possibly able to extract some useful functions from here like variance

    /*

	public float average(int start, int length)
	{
		float result = 0;
		for(int i = start; i > (start - length); i--)
		{
			Price p = get(i);
			Price prev = get(i-1);
			result += p.getPercentDiff(prev);
		}

		return (result / length);
	}

	@Deprecated
	public float getBeta(PriceList index, int start, int length)
	{
		if(get(0).date.equals(index.get(0).date) == false)
		{
			System.out.println("getBeta() start dates do not match");
			return 0;
		}

	    float result = covar(index,start,length);
	    result /= index.variance(start,length);

	    return result;
	}

	public float covar(PriceList b, int start, int length)
	{
		float result = 0;
		float avg_a = average(start,length);
		float avg_b = b.average(start,length);

		for(int i = start; i > (start - length); i--)
		{
			Price p1 = get(i);
			Price p1prev = get(i-1);
			Price p2 = b.get(i);
			Price p2prev = b.get(i-1);

			if(!p1.date.equals(p2.date))
				System.out.println("dates do not match");

			float s = p1.getPercentDiff(p1prev) - avg_a;
			float t = p2.getPercentDiff(p2prev) - avg_b;
			result += (s * t);
		}

		return (result / length);
	}
	*/

    override fun toString(): String {
        var result = "{"

        var i = 0
        while (i < size && i < 5) {
            result += get(i).toString() + ", "
            i++
        }

        result += "...}"
        return result
    }
}

fun List<Float>.toFloatArray(): FloatArray {
    val result = FloatArray(size)
    this.forEachIndexed { index, value -> result[index] = value }
    return result
}