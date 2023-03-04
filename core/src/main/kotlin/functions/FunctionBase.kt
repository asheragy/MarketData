package org.cerion.marketdata.core.functions

import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.functions.types.Overlay
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.reflect.KClass

//import kotlin.reflect.KClass

abstract class FunctionBase protected constructor(override val id: IFunctionEnum, vararg params: Number) : IFunction {

    private val _params: MutableList<Number>

    init {
        _params = removeDoubles(*params)
    }

    override val params: List<Number> = _params

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.ordinal
        result = prime * result + _params.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (other !is FunctionBase)
            return false
        if (this::class != other::class)
            return false

        return if (id !== other.id) false else _params == other._params

        // Finally, equal if parameters are equal
    }

    override fun toString(): String {
        // String.join isn't working on android with a Set, possibly java v7/8 issue
        var join = id.toString()

        var i = 0
        for (n in _params) {
            join += if (i > 0)
                ","
            else
                " "

            join += n
            i++
        }

        return join
    }

    protected fun getFloat(pos: Int): Float {
        return _params[pos].toFloat()
    }

    protected fun getInt(pos: Int): Int {
        return _params[pos].toInt()
    }

    override fun setParams(vararg params: Number) {
        if (_params.size != params.size)
            throw IllegalArgumentException("invalid parameter count")

        val newParams = removeDoubles(*params)

        for (i in newParams.indices) {
            if (newParams[i]::class != _params[i]::class)
                throw IllegalArgumentException("invalid parameter type at position $i")
        }

        newParams.forEachIndexed { index, number -> _params[index] = number }
    }

    override val resultType: KClass<*>
        get() {
            // Evaluate on a small dataset to get result type
            val fakeList = OHLCVTable.generateSeries(10) //
            val result = eval(fakeList)
            return result::class
        }

    private fun removeDoubles(vararg params: Number): MutableList<Number> {
        val result = params.toMutableList()

        for (i in result.indices) {
            if (result[i]::class == Double::class)
                result[i] = params[i].toFloat()
        }

        return result
    }

    override fun serialize(): String {
        var result = this.id.toString()
        if (params.isNotEmpty()) {
            result += "(" + params.joinToString(",") + ")"
        }

        return result
    }

    companion object {
        fun deserialize(str: String): IFunction {
            // Format is NAME(p1,p2)
            val tokens = str.replace("(", ",").replace(")", "").split(",")
            val name = tokens[0]
            val values = tokens.drop(1).map { parseNumber(it) }

            val enums: MutableList<IFunctionEnum> = Indicator.values().toMutableList()
            enums += Overlay.values().toList()
            enums += PriceOverlay.values().toList()

            val function = enums.first { it.toString() == name }
            val result = function.instance

            if (values.isNotEmpty())
                result.setParams(*values.toTypedArray())

            return result
        }

        private fun parseNumber(n: String): Number {
            return if (n.contains("."))
                n.toDouble()
            else
                n.toInt()
        }
    }
}
