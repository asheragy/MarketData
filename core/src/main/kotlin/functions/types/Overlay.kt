package org.cerion.marketdata.core.functions.types

import org.cerion.marketdata.core.functions.ISimpleOverlay
import org.cerion.marketdata.core.overlays.*

enum class Overlay : IFunctionEnum {

    EMA,
    SMA,
    BB,
    KAMA,
    LINE,
    LINREG;

    override val instance: ISimpleOverlay
        get() {
            return when (this) {
                EMA -> ExpMovingAverage()
                SMA -> SimpleMovingAverage()
                BB -> BollingerBands()
                KAMA -> KAMA()
                LINE -> Line()
                LINREG -> LinearRegressionLine()
            }
        }

    override fun getInstance(vararg params: Number): ISimpleOverlay {
        val overlay = instance
        overlay.setParams(*params)
        return overlay
    }
}
