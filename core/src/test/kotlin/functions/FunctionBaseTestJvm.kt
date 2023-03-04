package org.cerion.marketdata.core.functions;

import org.cerion.marketdata.core.overlays.BollingerBands
import org.junit.Test;
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FunctionBaseTestJvm {

    @Test
    fun setParams_tooMany() {
        assertFailsWith<IllegalArgumentException> {
            val call = BollingerBands(20, 2.0)
            call.setParams(20, 10, 10)
        }
    }

    @Test
    fun setParams_tooFew() {
        assertFailsWith<IllegalArgumentException> {
            val call = BollingerBands(20, 2.0)
            call.setParams(20)
        }
    }

    @Test
    fun setParams_typeMismatch() {
        assertFailsWith<IllegalArgumentException> {
            val call = BollingerBands(20, 2.0)
            call.setParams(20, 10)
        }
    }

    @Test
    fun setParams_convertsDoubleToFloat() {
        val call = BollingerBands()
        assertEquals(Float::class, call.params[1]::class)

        call.setParams(20, 2.0)
        assertEquals(Float::class, call.params[1]::class)
    }

    @Test
    fun setParams_setsNewValues() {
        val call = BollingerBands()
        call.setParams(33, 2.5)

        assertEquals(33, call.params[0])
        assertEquals(2.5f, call.params[1])
    }
}
