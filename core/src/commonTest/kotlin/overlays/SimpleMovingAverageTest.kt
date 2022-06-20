package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.TestBase
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleMovingAverageTest : TestBase() {

    @Test
    fun sma_1() = runPriceTest {
        val arr = it.close
        val sma = arr.sma(1)
        assertEquals(4025, sma.size, "Unexpected test arrays length")

        for (i in 0 until sma.size)
            assertEquals(arr[i], sma[i], "position $i")
    }

    @Test
    fun sma_2() = runPriceTest {
        val sma = it.close.sma(2)

        assertEquals(4025, sma.size, "Unexpected test arrays length")
        assertEquals(1455.22, sma[0], "position 0")
        assertEquals(1427.32, sma[1], "position 1")
        assertEquals(1429.40, sma[sma.size / 2], "position " + sma.size / 2)
        assertEquals(2053.65, sma[sma.size - 1], "position last")
    }

    @Test
    fun sma_20() = runPriceTest {
        val sma = it.close.sma(20)

        assertEquals(4025, sma.size, "Unexpected test arrays length")
        assertEquals(1455.22, sma[0], "position 0")
        assertEquals(1427.32, sma[1], "position 1")
        assertEquals(1473.51, sma[sma.size / 2], "position " + sma.size / 2)
        assertEquals(2050.38, sma[sma.size - 1], "position last")
    }

    @Test
    fun sma_200() = runPriceTest {
        val sma = it.close.sma(200)

        assertEquals(4025, sma.size.toLong(), "Unexpected test arrays length")
        assertEquals(1455.22, sma[0], "position 0")
        assertEquals(1427.32, sma[1], "position 1")
        assertEquals(1490.95, sma[sma.size / 2], "position " + sma.size / 2)
        assertEquals(2061.15, sma[sma.size - 1], "position last")
    }

    @Test
    fun sma_usesHighestAverage() = runPriceTest {
        val sma20 = it.close.sma(20)
        val sma100 = it.close.sma(100)
        val sma200 = it.close.sma(200)

        for (i in 0..19)
            assertEquals(sma20[i], sma100[i], "20 and 100 position $i")

        for (i in 0..19)
            assertEquals(sma20[i], sma200[i], "20 and 200 position $i")

        for (i in 0..99)
            assertEquals(sma100[i], sma200[i], "100 and 200 position $i")
    }
}