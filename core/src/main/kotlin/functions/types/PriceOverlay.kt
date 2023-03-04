package org.cerion.marketdata.core.functions.types

import org.cerion.marketdata.core.functions.IPriceOverlay
import org.cerion.marketdata.core.overlays.*

enum class PriceOverlay : IFunctionEnum {

    CHAN, // price channels
    CEXIT,
    KC,
    PSAR,
    CLOUD, // Ichimoku Clouds
    ZIGZAG,
    VWMA;

    override val instance: IPriceOverlay
        get() {
            return when (this) {
                CHAN -> PriceChannels()
                CLOUD -> IchimokuClouds()
                CEXIT -> ChandelierExit()
                KC -> KeltnerChannels()
                PSAR -> ParabolicSAR()
                ZIGZAG -> ZigZag()
                VWMA -> VolumeWeightedMovingAverage()
            }

        }

    override fun getInstance(vararg params: Number): IPriceOverlay {
        val overlay = instance
        overlay.setParams(*params)
        return overlay
    }
}
