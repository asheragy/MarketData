package org.cerion.marketdata.core.arrays

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.overlays.SimpleMovingAverage
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatArrayTest : TestBase() {

    @Test
    fun sma_callsSimpleMovingAverage() = runPriceTest {
        val arr = it.close
        val arr1 = SimpleMovingAverage(13).eval(arr)
        val arr2 = arr.sma(13)

        assertEquals(arr1.last, arr2.last, "all elements should be equal")
    }

    @Test
    fun slope() = runPriceTest {
        val arr = it.close
        assertEquals(0.0, arr.slope(5, 0), "slope position 0")
        assertEquals(-55.8, arr.slope(5, 1), "slope position 1")
        assertEquals(-2.35, arr.slope(5, 4), "slope position 4")
        assertEquals(15.57, arr.slope(5, 5), "slope position 5")
        assertEquals(-2.72, arr.slope(5, arr.size - 1), "slope position last") // stockcharts verified
    }

    @Test
    fun regressionLine() = runPriceTest {
        val arr = it.close
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
    fun sum() = runPriceTest {
        val arr = it.close
        assertEquals(1455.22, arr.sum(0, 0))
        assertEquals(2854.64, arr.sum(0, 1))
        assertEquals(8698.38, arr.sum(5, 10))
    }

    @Test
    fun std()= runPriceTest {
        val period = 10
        val std = it.close.std(period)

        assertEquals(0.0, std.first, "standard deviation position 0")
        assertEquals(27.90, std[1], "standard deviation position 1")
        assertEquals(23.34, std[period - 1], "standard deviation position p-1")
        assertEquals(23.33, std[period], "standard deviation position p")
        assertEquals(20.78, std.last, "standard deviation position last")
    }

    @Test
    fun correlation() = runPriceTest {
        val arr = it.close
        var corr = arr.correlation(arr)
        assertEquals(1.0, corr, "correlation self")
        assertEquals(1.0, corr, 0.000001, "correlation self")

        corr = arr.correlation(it.high)
        // TODO look into js/jvm discrepancy
        assertEquals(0.9995274, corr, 0.00001, "correlation high")

        corr = arr.correlation(it.volume)
        assertEquals(0.06938858, corr, 0.0001, "correlation volume")
    }
}
