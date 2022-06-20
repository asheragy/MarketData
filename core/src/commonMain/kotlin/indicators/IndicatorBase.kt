package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.functions.FunctionBase
import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.functions.types.IFunctionEnum
import org.cerion.marketdata.core.functions.types.Indicator

abstract class IndicatorBase internal constructor(id: IFunctionEnum, vararg params: Number) : FunctionBase(id, *params), IIndicator {
    override val id: Indicator = super.id as Indicator
}
