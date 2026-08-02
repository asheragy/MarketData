package train

import org.cerion.marketdata.core.indicators.*
import org.cerion.marketdata.core.overlays.ExpMovingAverage

data class InputData(
    val expr: SeriesExpr,
    val quantiles: Int = 5
)

val Inputs = listOf(
    InputData(expr = FuncExpr(RSI(3))),
    InputData(expr = FuncExpr(RSI(7))),
    InputData(expr = FuncExpr(RSI(14))),
    InputData(expr = FuncExpr(RSI(14)) - LagExpr(FuncExpr(RSI(14)), 1)),
    InputData(expr = FuncExpr(RSI(7)) - FuncExpr(RSI(14))),
    InputData(expr = FuncExpr(RSI(14)) - FuncExpr(IndexTable, RSI(14))),
    InputData(expr = FuncExpr(RSI(14)) / FuncExpr(FuncExpr(RSI(14)), ExpMovingAverage(3))),
    InputData(expr = FuncExpr(RSI(14)) - FuncExpr(FuncExpr(RSI(14)),  ExpMovingAverage(3))),
    InputData(expr = FuncExpr(TrueStrengthIndex())),
    InputData(expr = FuncExpr(TRIX())),
    InputData(expr = FuncExpr(Stochastic())),
    InputData(expr = FuncExpr(PringsKnowSureThing())),
    InputData(expr = FuncExpr(CommodityChannelIndex())),
    InputData(expr = FuncExpr(ChaikinMoneyFlow())),
    InputData(expr = FuncExpr(AverageDirectionalIndex())),

    InputData(expr = CustomExpr("RSI/EMA Crossover", { ctx ->
        val rsi = RSI(14).eval(ctx.table)
        val ema = ExpMovingAverage(3).eval(rsi)

        labels = MutableList(ctx.table.size) { "" }
        for(i in ctx.table.indices) {
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
            for(i in ctx.table.indices) {
                if (price[i] < ema[i])
                    labels[i] = "Price < EMA"
                else
                    labels[i] = "Price > EMA"
            }

            RSI(14).eval(ctx.table)
        }),
        quantiles = 2
    ),
    InputData(
        expr = CustomExpr("RSI 14 Bollinger Bands", { ctx ->
            RSI(14).eval(ctx.table).bb(20, 2.0f).percent()
        }),
        quantiles = 10
    ),

    InputData(expr = CustomExpr("RSI(stock / SPY, 14)", { ctx ->
        val series = ctx.table.close.divide(ctx.index.close)
        RSI(14).eval(series)
    }))
)
