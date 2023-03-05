package org.cerion.marketdata.core.overlays


import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.FunctionBase
import org.cerion.marketdata.core.functions.IPriceOverlay
import org.cerion.marketdata.core.functions.ISimpleOverlay
import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.model.OHLCVTable

abstract class PriceOverlayBase internal constructor(id: IFunctionEnum, vararg params: Number) : FunctionBase(id, *params), IPriceOverlay

abstract class OverlayBase<T : ValueArray<*>> internal constructor(id: IFunctionEnum, vararg params: Number) : PriceOverlayBase(id, *params), ISimpleOverlay {

    override fun eval(table: OHLCVTable): T {
        return eval(table.close)
    }

    abstract override fun eval(arr: FloatArray): T
}
