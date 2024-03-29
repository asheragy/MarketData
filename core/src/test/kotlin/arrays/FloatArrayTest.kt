package org.cerion.marketdata.core.arrays

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.Test

class FloatArrayTest : TestBase() {

    @Test
    fun sma_callsSimpleMovingAverage() {
        val arr = table.close
        val arr1 = SimpleMovingAverage(13).eval(arr)
        val arr2 = arr.sma(13)

        assertEquals(arr1.last, arr2.last, "all elements should be equal")
    }

    @Test
    fun slope() {
        val arr = table.close
        assertEquals(0.0, arr.slope(5, 0), "slope position 0")
        assertEquals(-55.8, arr.slope(5, 1), "slope position 1")
        assertEquals(-2.35, arr.slope(5, 4), "slope position 4")
        assertEquals(15.57, arr.slope(5, 5), "slope position 5")
        assertEquals(-2.72, arr.slope(5, arr.size - 1), "slope position last") // stockcharts verified
    }

    @Test
    fun regressionLine() {
        val arr = table.close
        assertEquals(1455.22, arr.regressionLinePoint(5, 0), "regressionLine position 0")
        assertEquals(1483.12, arr.regressionLinePoint(5, 1), "regressionLine position 1")
        assertEquals(1422.68, arr.regressionLinePoint(5, 4), "regressionLine position 4")
        assertEquals(1405.24, arr.regressionLinePoint(5, 5), "regressionLine position 5")
        assertEquals(2063.35, arr.regressionLinePoint(5, arr.size - 1), "regressionLine position last")
    }

    @Test
    fun zeroLengthArrays() {
        val arr = FloatArray(0)
        arr.ema(20) // No exception thrown
    }

    @Test
    fun sum() {
        val arr = table.close
        assertEquals(1455.22, arr.sum(0, 0))
        assertEquals(2854.64, arr.sum(0, 1))
        assertEquals(8698.38, arr.sum(5, 10))
    }

    @Test
    fun std() {
        val period = 10
        val std = table.close.std(period)

        assertEquals(0.0, std.first, "standard deviation position 0")
        assertEquals(27.90, std[1], "standard deviation position 1")
        assertEquals(23.34, std[period - 1], "standard deviation position p-1")
        assertEquals(23.33, std[period], "standard deviation position p")
        assertEquals(20.78, std.last, "standard deviation position last")
    }

    @Test
    fun correlation() {
        val arr = table.close
        var corr = arr.correlation(arr)
        assertEquals(1.0, corr, "correlation self")
        assertEquals(1.0, corr, 0.000001, "correlation self")

        corr = arr.correlation(table.high)
        assertEquals(0.9995274, corr, 0.00001, "correlation high")

        corr = arr.correlation(table.volume)
        assertEquals(0.06938858, corr, 0.0001, "correlation volume")
    }

    @Test
    fun variance() {
        val input = floatArrayOf(1.21f, 3.4f, 2f, 4.66f, 1.5f, 5.61f, 7.22f)
        val arr = FloatArray(input)

        assertEquals(5.16122, arr.variance(7).last)
        assertEquals(3.23155, arr.variance(7)[5])
        assertEquals(2.39805, arr.variance(7)[1])
        assertEquals(0, arr.variance(7)[0])

        assertEquals(5.86252, arr.variance(5).last)
        assertEquals(3.00898, arr.variance(5)[5])
        assertEquals(2.39805, arr.variance(5)[1])
        assertEquals(0, arr.variance(5)[0])
    }

    @Test
    fun covariance() {
        val arr1 = FloatArray(floatArrayOf(2f, 1.5f, 3f, 5f, 10f))
        val arr2 = FloatArray(floatArrayOf(5f, 2f, 1.25f, 15f, 8f))

        val covar = arr1.covariance(arr2, 5)
        assertEquals(9.34375, covar.last)
        assertEquals(8.30208, covar[3])
        assertEquals(0.75, covar[1])
        assertEquals(0, covar[0])
    }

    @Test
    fun roc() {
        val roc = table22.close.roc(12)
        assertNotSet(roc.first)
        assertNotSet(roc[11])
        assertEquals(-1.46, roc[12])
        assertEquals(-4.48, roc.last)
    }
}
