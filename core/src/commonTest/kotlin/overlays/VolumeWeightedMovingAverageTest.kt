package overlays

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.overlays.VolumeWeightedMovingAverage
import org.junit.Test


class VolumeWeightedMovingAverageTest : TestBase() {

    @Test
    fun test() = runPriceTest {
        val arr = VolumeWeightedMovingAverage(20).eval(it)

        assertNotSet(arr.first)
        assertNotSet(arr[18])
        assertEquals(1425.45, arr[19], "p19")
        assertEquals(2047.56, arr.last, "last")
    }
}