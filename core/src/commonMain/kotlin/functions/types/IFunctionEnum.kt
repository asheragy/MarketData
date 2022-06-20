package org.cerion.marketdata.core.functions.types

import org.cerion.marketdata.core.functions.IFunction

interface IFunctionEnum {
    val instance: IFunction
    val ordinal: Int
    fun getInstance(vararg params: Number): IFunction
}
