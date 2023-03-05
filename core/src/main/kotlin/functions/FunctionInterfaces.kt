package org.cerion.marketdata.core.functions

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.reflect.KClass


interface IFunction {
    val name: String
    val resultType: KClass<*>
    val id: IFunctionEnum
    val params: List<Number>
    fun eval(table: OHLCVTable): ValueArray<*>
    fun setParams(vararg params: Number)
    fun serialize(): String
}

interface IIndicator : IFunction {
    override val id: Indicator
}

interface IOverlay : IFunction

interface IPriceOverlay : IOverlay

interface ISimpleOverlay : IOverlay {
    fun eval(arr: org.cerion.marketdata.core.arrays.FloatArray): ValueArray<*>
}
