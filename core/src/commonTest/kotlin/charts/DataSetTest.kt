package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.TestBase
import org.cerion.marketdata.core.arrays.FloatArray
import kotlin.test.Test
import kotlin.test.assertEquals

class DataSetTest : TestBase() {

    @Test
    fun sizeOffsetByOne() {
        val arr = FloatArray(5)
        val data = DataSet(arr, "", 0)

        assertEquals(arr.size - 1, data.size, "size should be 1 less")
        assertEquals(arr[1], data[0], 0.0001, "invalid value at position 0")
    }

    @Test
    fun dataSet_iterator() {
        val arr = FloatArray(5) // Length 5 but iterator skips first element and returns 4
        for(i in 0 until 5)
            arr[i] = (i * i).toFloat()

        val data = DataSet(arr, "", 0)
        val mapped = data.mapIndexed { index: Int, value: Float ->
            Pair(index, value)
        }

        assertEquals(4, mapped.size)
        assertEquals(Pair(0, 1.0f), mapped[0])
        assertEquals(Pair(3, 16.0f), mapped[3])
    }
}