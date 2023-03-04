package org.cerion.marketdata.core.functions.types

import org.cerion.marketdata.core.functions.IIndicator
import org.cerion.marketdata.core.indicators.*

enum class Indicator : IFunctionEnum {

    MACD,
    RSI,
    ADX,
    AROON,
    ATR,
    CCI,
    DI,
    KST,
    MASS_INDEX,
    PMO,
    PPO,
    SPECIALK, // prings specialK
    STOCH,
    STOCHRSI,
    TRIX,
    TSI,
    ULCER_INDEX,
    UO,
    VORTEX,
    WPR,
    SHARPE,

    // Volume Indicators
    ADL,
    CMF,
    CO,
    EMV,
    FORCE_INDEX,
    MFI,
    NVI,
    OBV,
    PVO;

    override val instance: IIndicator
        get() {
            return when (this) {
                MACD -> MACD()
                RSI -> RSI()
                ADX -> AverageDirectionalIndex()
                FORCE_INDEX -> ForceIndex()
                ATR -> AverageTrueRange()
                STOCHRSI -> StochasticRSI()
                CCI -> CommodityChannelIndex()
                MFI -> MoneyFlowIndex()
                CMF -> ChaikinMoneyFlow()
                MASS_INDEX -> MassIndex()
                TRIX -> TRIX()
                ULCER_INDEX -> UlcerIndex()
                EMV -> EaseOfMovement()
                OBV -> OnBalanceVolume()
                ADL -> AccumulationDistributionLine()
                NVI -> NegativeVolumeIndex()
                WPR -> WilliamsPercentR()
                VORTEX -> Vortex()
                UO -> UltimateOscillator()
                TSI -> TrueStrengthIndex()
                KST -> PringsKnowSureThing()
                CO -> ChaikinOscillator()
                SPECIALK -> PringsSpecialK()
                PMO -> PriceMomentumOscillator()
                AROON -> AroonUpDown()
                DI -> DirectionalIndex()
                PVO -> PercentageVolumeOscillator()
                PPO -> PercentagePriceOscillator()
                STOCH -> Stochastic()
                SHARPE -> SharpeRatio()
            }
        }

    override fun getInstance(vararg params: Number): IIndicator {
        val instance = instance
        instance.setParams(*params)
        return instance
    }
}
