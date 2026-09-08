package train

import org.cerion.marketdata.core.indicators.*
import org.cerion.marketdata.core.overlays.*
import org.cerion.marketdata.core.series.BandSeries
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.series.MACDSeries
import org.cerion.marketdata.core.series.PairSeries

data class InputData(
    val expr: SeriesExpr,
    val quantiles: Int = 3
)

private fun macdValueInput(name: String, evalFn: (EvalContext) -> MACDSeries) =
    InputData(expr = CustomExpr(name) { ctx -> evalFn(ctx).macd })

private fun macdHistInput(name: String, evalFn: (EvalContext) -> MACDSeries) =
    InputData(expr = CustomExpr(name) { ctx -> evalFn(ctx).hist })

private fun pairDiffInput(name: String, evalFn: (EvalContext) -> PairSeries) =
    InputData(expr = CustomExpr(name) { ctx ->
        val pair = evalFn(ctx)
        val result = FloatSeries(ctx.size)
        for (i in 0 until ctx.size)
            result[i] = pair.diff(i)

        result
    })

private fun bandPercentInput(name: String, evalFn: (EvalContext) -> BandSeries, quantiles: Int = 5) =
    InputData(
        expr = CustomExpr(name) { ctx -> evalFn(ctx).percent() },
        quantiles = quantiles
    )

private fun bandWidthInput(name: String, evalFn: (EvalContext) -> BandSeries) =
    InputData(expr = CustomExpr(name) { ctx ->
        val bands = evalFn(ctx)
        val result = FloatSeries(ctx.size)
        for (i in 0 until ctx.size)
            result[i] = bands.bandwidth(i)

        result
    })

val Inputs = listOf(
    // RSI and RSI-derived momentum
    InputData(expr = FuncExpr(RSI())),
    InputData(expr = FuncExpr(RSI(3))),
    InputData(expr = FuncExpr(RSI(7))),
    InputData(expr = FuncExpr(RSI(21))),
    InputData(expr = FuncExpr(RSI(14)) - LagExpr(FuncExpr(RSI(14)), 1)),
    InputData(expr = FuncExpr(RSI(7)) - FuncExpr(RSI(14))),
    InputData(expr = FuncExpr(RSI(14)) - FuncExpr(IndexTable, RSI(14))),
    InputData(expr = FuncExpr(RSI(14)) / FuncExpr(FuncExpr(RSI(14)), ExpMovingAverage(3))),
    InputData(expr = FuncExpr(RSI(14)) - FuncExpr(FuncExpr(RSI(14)), ExpMovingAverage(3))),
    InputData(expr = FuncExpr(StochasticRSI())),
    InputData(expr = FuncExpr(StochasticRSI(7))),
    InputData(expr = FuncExpr(StochasticRSI(21))),

    InputData(expr = CustomExpr("RSI/EMA Crossover", { ctx ->
        val rsi = RSI(14).eval(ctx.table)
        val ema = ExpMovingAverage(3).eval(rsi)

        labels = MutableList(ctx.table.size) { "" }
        for (i in ctx.table.indices) {
            // RSI(14) - EMA(RSI(14), 3)
            val diff = rsi[i] - ema[i]
            if (diff > 0)
                labels[i] = "RSI > EMA"
            else
                labels[i] = "RSI < EMA"
        }

        rsi
    }),
        quantiles = 3),
    InputData(
        // Would be EMA200 for daily
        expr = CustomExpr("RSI(14) when price <> EMA(price, 50)", { ctx ->
            val ema = ExpMovingAverage(50).eval(ctx.table)
            val price = ctx.table.close

            labels = MutableList(ctx.table.size) { "" }
            for (i in ctx.table.indices) {
                if (price[i] < ema[i])
                    labels[i] = "Price < EMA"
                else
                    labels[i] = "Price > EMA"
            }

            RSI(14).eval(ctx.table)
        }),
        quantiles = 2
    ),
    bandPercentInput("RSI(14) Bollinger Bands %B", { ctx ->
        RSI(14).eval(ctx.table).bb(20, 2.0f)
    }, quantiles = 3),
    InputData(expr = CustomExpr("RSI(stock / SPY, 14)", { ctx ->
        val series = ctx.table.close.divide(ctx.index.close)
        RSI(14).eval(series)
    })),

    // MACD-style price and volume oscillators
    macdHistInput("MACD() histogram", { ctx -> MACD().eval(ctx.table) }),
    macdValueInput("MACD() line", { ctx -> MACD().eval(ctx.table) }),
    macdHistInput("MACD(8, 17, 9) histogram", { ctx -> MACD(8, 17, 9).eval(ctx.table) }),
    macdHistInput("MACD(19, 39, 9) histogram", { ctx -> MACD(19, 39, 9).eval(ctx.table) }),
    macdHistInput("PPO() histogram", { ctx -> PercentagePriceOscillator().eval(ctx.table) }),
    macdValueInput("PPO() line", { ctx -> PercentagePriceOscillator().eval(ctx.table) }),
    macdHistInput("PPO(6, 13, 9) histogram", { ctx -> PercentagePriceOscillator(6, 13, 9).eval(ctx.table) }),
    macdHistInput("PPO(24, 52, 9) histogram", { ctx -> PercentagePriceOscillator(24, 52, 9).eval(ctx.table) }),
    macdHistInput("PVO() histogram", { ctx -> PercentageVolumeOscillator().eval(ctx.table) }),
    macdValueInput("PVO() line", { ctx -> PercentageVolumeOscillator().eval(ctx.table) }),
    macdHistInput("PVO(6, 13, 9) histogram", { ctx -> PercentageVolumeOscillator(6, 13, 9).eval(ctx.table) }),
    macdHistInput("PVO(24, 52, 9) histogram", { ctx -> PercentageVolumeOscillator(24, 52, 9).eval(ctx.table) }),

    // Trend strength and directional movement
    InputData(expr = FuncExpr(AverageDirectionalIndex())),
    InputData(expr = FuncExpr(AverageDirectionalIndex(7))),
    InputData(expr = FuncExpr(AverageDirectionalIndex(28))),
    pairDiffInput("DirectionalIndex() +DI - -DI", { ctx -> DirectionalIndex().eval(ctx.table) }),
    pairDiffInput("DirectionalIndex(7) +DI - -DI", { ctx -> DirectionalIndex(7).eval(ctx.table) }),
    pairDiffInput("DirectionalIndex(28) +DI - -DI", { ctx -> DirectionalIndex(28).eval(ctx.table) }),
    pairDiffInput("AroonUpDown() up - down", { ctx -> AroonUpDown().eval(ctx.table) }),
    pairDiffInput("AroonUpDown(14) up - down", { ctx -> AroonUpDown(14).eval(ctx.table) }),
    pairDiffInput("AroonUpDown(50) up - down", { ctx -> AroonUpDown(50).eval(ctx.table) }),
    pairDiffInput("Vortex() +VI - -VI", { ctx -> Vortex().eval(ctx.table) }),
    pairDiffInput("Vortex(7) +VI - -VI", { ctx -> Vortex(7).eval(ctx.table) }),
    pairDiffInput("Vortex(28) +VI - -VI", { ctx -> Vortex(28).eval(ctx.table) }),
    InputData(
        expr = CustomExpr("ADX(14) by DI direction", { ctx ->
            val di = DirectionalIndex(14).eval(ctx.table)

            labels = MutableList(ctx.table.size) { "" }
            for (i in ctx.table.indices) {
                if (di.diff(i) > 0)
                    labels[i] = "+DI > -DI"
                else
                    labels[i] = "-DI > +DI"
            }

            AverageDirectionalIndex(14).eval(ctx.table)
        }),
        quantiles = 3
    ),

    // Stochastic and bounded oscillators
    InputData(expr = FuncExpr(Stochastic())),
    InputData(expr = FuncExpr(Stochastic(5, 3, 3))),
    InputData(expr = FuncExpr(Stochastic(21, 5, 5))),
    InputData(expr = FuncExpr(WilliamsPercentR())),
    InputData(expr = FuncExpr(WilliamsPercentR(7))),
    InputData(expr = FuncExpr(WilliamsPercentR(28))),
    InputData(expr = FuncExpr(UltimateOscillator())),
    InputData(expr = FuncExpr(UltimateOscillator(4, 8, 16))),
    InputData(expr = FuncExpr(UltimateOscillator(14, 28, 56))),
    InputData(expr = FuncExpr(MoneyFlowIndex())),
    InputData(expr = FuncExpr(MoneyFlowIndex(7))),
    InputData(expr = FuncExpr(MoneyFlowIndex(28))),
    InputData(
        expr = CustomExpr("MFI(14) by price <> EMA(price, 50)", { ctx ->
            val ema = ExpMovingAverage(50).eval(ctx.table)
            val price = ctx.table.close

            labels = MutableList(ctx.table.size) { "" }
            for (i in ctx.table.indices) {
                if (price[i] < ema[i])
                    labels[i] = "Price < EMA"
                else
                    labels[i] = "Price > EMA"
            }

            MoneyFlowIndex(14).eval(ctx.table)
        }),
        quantiles = 2
    ),

    // Momentum and rate-of-change indicators
    InputData(expr = FuncExpr(TrueStrengthIndex())),
    InputData(expr = FuncExpr(TrueStrengthIndex(13, 7))),
    InputData(expr = FuncExpr(TrueStrengthIndex(50, 25))),
    InputData(expr = FuncExpr(TRIX())),
    InputData(expr = FuncExpr(TRIX(9))),
    InputData(expr = FuncExpr(TRIX(30))),
    InputData(expr = FuncExpr(PriceMomentumOscillator())),
    InputData(expr = FuncExpr(PriceMomentumOscillator(20, 10))),
    InputData(expr = FuncExpr(PriceMomentumOscillator(50, 35))),
    InputData(expr = FuncExpr(CoppockCurve())),
    InputData(expr = FuncExpr(CoppockCurve(7, 5, 5))),
    InputData(expr = FuncExpr(CoppockCurve(20, 14, 10))),
    InputData(expr = FuncExpr(PringsKnowSureThing())),
    InputData(expr = FuncExpr(PringsKnowSureThing(5, 10, 15, 20, 5, 5, 10, 10))),
    InputData(expr = FuncExpr(PringsKnowSureThing(15, 20, 30, 45, 10, 10, 15, 20))),
    InputData(expr = FuncExpr(PringsSpecialK())),

    // Volatility, range, and channel position
    InputData(expr = FuncExpr(AverageTrueRange())),
    InputData(expr = FuncExpr(AverageTrueRange(7))),
    InputData(expr = FuncExpr(AverageTrueRange(28))),
    InputData(expr = FuncExpr(CommodityChannelIndex())),
    InputData(expr = FuncExpr(CommodityChannelIndex(10))),
    InputData(expr = FuncExpr(CommodityChannelIndex(40))),
    InputData(expr = FuncExpr(MassIndex())),
    InputData(expr = FuncExpr(MassIndex(9))),
    InputData(expr = FuncExpr(MassIndex(50))),
    InputData(expr = FuncExpr(UlcerIndex(7))),
    InputData(expr = FuncExpr(UlcerIndex(14))),
    InputData(expr = FuncExpr(UlcerIndex(28))),
    bandPercentInput("Close BollingerBands() %B", { ctx -> BollingerBands().eval(ctx.table.close) }),
    bandWidthInput("Close BollingerBands() bandwidth", { ctx -> BollingerBands().eval(ctx.table.close) }),
    bandPercentInput("Close BollingerBands(10, 1.5) %B", { ctx -> BollingerBands(10, 1.5).eval(ctx.table.close) }),
    bandPercentInput("Close BollingerBands(50, 2.5) %B", { ctx -> BollingerBands(50, 2.5).eval(ctx.table.close) }),
    bandPercentInput("PriceChannels() %B", { ctx -> PriceChannels().eval(ctx.table) }),
    bandWidthInput("PriceChannels() bandwidth", { ctx -> PriceChannels().eval(ctx.table) }),
    bandPercentInput("PriceChannels(10) %B", { ctx -> PriceChannels(10).eval(ctx.table) }),
    bandPercentInput("PriceChannels(55) %B", { ctx -> PriceChannels(55).eval(ctx.table) }),
    bandPercentInput("KeltnerChannels() %B", { ctx -> KeltnerChannels().eval(ctx.table) }),
    bandWidthInput("KeltnerChannels() bandwidth", { ctx -> KeltnerChannels().eval(ctx.table) }),
    bandPercentInput("KeltnerChannels(10, 1.5, 7) %B", { ctx -> KeltnerChannels(10, 1.5, 7).eval(ctx.table) }),
    bandPercentInput("KeltnerChannels(50, 2.5, 20) %B", { ctx -> KeltnerChannels(50, 2.5, 20).eval(ctx.table) }),
    pairDiffInput("ChandelierExit() long - short", { ctx -> ChandelierExit().eval(ctx.table) }),
    pairDiffInput("ChandelierExit(10, 2.0) long - short", { ctx -> ChandelierExit(10, 2.0).eval(ctx.table) }),
    pairDiffInput("ChandelierExit(50, 4.0) long - short", { ctx -> ChandelierExit(50, 4.0).eval(ctx.table) }),
    pairDiffInput("IchimokuClouds() span A - span B", { ctx -> IchimokuClouds().eval(ctx.table) }),
    pairDiffInput("IchimokuClouds(6, 18, 36) span A - span B", { ctx -> IchimokuClouds(6, 18, 36).eval(ctx.table) }),
    pairDiffInput("IchimokuClouds(12, 36, 72) span A - span B", { ctx -> IchimokuClouds(12, 36, 72).eval(ctx.table) }),

    // Moving averages and price overlays
    InputData(expr = FieldExpr("close") / FuncExpr(ExpMovingAverage())),
    InputData(expr = FieldExpr("close") / FuncExpr(ExpMovingAverage(10))),
    InputData(expr = FieldExpr("close") / FuncExpr(ExpMovingAverage(50))),
    InputData(expr = FieldExpr("close") / FuncExpr(SimpleMovingAverage())),
    InputData(expr = FieldExpr("close") / FuncExpr(SimpleMovingAverage(20))),
    InputData(expr = FieldExpr("close") / FuncExpr(SimpleMovingAverage(200))),
    InputData(expr = FieldExpr("close") / FuncExpr(KAMA())),
    InputData(expr = FieldExpr("close") / FuncExpr(KAMA(5, 2, 20))),
    InputData(expr = FieldExpr("close") / FuncExpr(KAMA(20, 2, 50))),
    InputData(expr = FieldExpr("close") / FuncExpr(VolumeWeightedMovingAverage())),
    InputData(expr = FieldExpr("close") / FuncExpr(VolumeWeightedMovingAverage(10))),
    InputData(expr = FieldExpr("close") / FuncExpr(VolumeWeightedMovingAverage(50))),
    InputData(expr = FieldExpr("close") / FuncExpr(ParabolicSAR())),
    InputData(expr = FieldExpr("close") / FuncExpr(ParabolicSAR(0.01, 0.1))),
    InputData(expr = FieldExpr("close") / FuncExpr(ParabolicSAR(0.04, 0.4))),
    //InputData(expr = FieldExpr("close") / FuncExpr(ZigZag())),
    //InputData(expr = FieldExpr("close") / FuncExpr(ZigZag(3.0))),
    //InputData(expr = FieldExpr("close") / FuncExpr(ZigZag(10.0))),
    InputData(expr = FieldExpr("close") / FuncExpr(Line())),
    InputData(expr = FieldExpr("close") / FuncExpr(Line(0.5))),
    InputData(expr = FieldExpr("close") / FuncExpr(Line(2.0))),
    InputData(expr = FieldExpr("close") / FuncExpr(LinearRegressionLine())),

    // Volume and accumulation indicators
    InputData(expr = FuncExpr(ChaikinMoneyFlow())),
    InputData(expr = FuncExpr(ChaikinMoneyFlow(10))),
    InputData(expr = FuncExpr(ChaikinMoneyFlow(40))),
    InputData(expr = FuncExpr(ChaikinOscillator())),
    InputData(expr = FuncExpr(ChaikinOscillator(3, 20))),
    InputData(expr = FuncExpr(ChaikinOscillator(10, 30))),
    InputData(expr = FuncExpr(EaseOfMovement())),
    InputData(expr = FuncExpr(EaseOfMovement(7))),
    InputData(expr = FuncExpr(EaseOfMovement(28))),
    InputData(expr = FuncExpr(ForceIndex())),
    InputData(expr = FuncExpr(ForceIndex(2))),
    InputData(expr = FuncExpr(ForceIndex(21))),
    InputData(expr = FuncExpr(AccumulationDistributionLine())),
    InputData(expr = FuncExpr(OnBalanceVolume())),
    InputData(expr = FuncExpr(NegativeVolumeIndex())),

    // Misc less-used indicators
    InputData(expr = FuncExpr(BalanceOfPower())),
    InputData(expr = FuncExpr(BalanceOfPower(7))),
    InputData(expr = FuncExpr(BalanceOfPower(28))),
    InputData(expr = FuncExpr(SharpeRatio())),
    InputData(expr = FuncExpr(SharpeRatio(5, 0.75))),
    InputData(expr = FuncExpr(SharpeRatio(20, 2.0)))
)
