package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.functions.types.PriceOverlay
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceOverlayBaseTest {

    @Test
    fun correctEnumReturned() {
        for (o in PriceOverlay.values()) {
            val overlay = o.instance
            assertEquals(o, overlay.id, "enum does not match instance")
        }
    }
}