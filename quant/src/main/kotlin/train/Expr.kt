package train

import data.SectorETFDef
import data.TextDataRepository
import org.cerion.marketdata.core.series.FloatSeries
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.ExpMovingAverage



fun main() {
    val dataSource = TextDataRepository()
    val dataSet = dataSource.get(SectorETFDef())
    val index = dataSet.index!!

    val rsi: Expr = CallExpr("RSI", 2)
    val rsi2: Expr = CallExpr("RSI", 2)
    val ctx = EvalContext(index, functions)

    ctx.eval(rsi)
    ctx.eval(rsi2)



    println(rsi)
}

sealed interface Expr {
    fun eval(ctx: EvalContext): FloatSeries
}

data class NumberExpr(val value: Number) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries =
        FloatSeries(ctx.size)// { value }
}

data class FieldExpr(val name: String) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries =
        ctx.series(name)
}

data class CallExpr(
    val name: String,
    val args: List<Expr>
) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries =
        ctx.call(name, args)

    constructor(name: String, vararg args: Number) : this(name, args.map { NumberExpr(it) }) {}
}

data class BinaryExpr(
    val left: Expr,
    val op: Op,
    val right: Expr
) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries {
        val a = ctx.eval(left)
        val b = ctx.eval(right)

        return FloatSeries(ctx.size) /*{ i ->
            when (op) {
                Op.ADD -> a[i] + b[i]
                Op.SUB -> a[i] - b[i]
                Op.MUL -> a[i] * b[i]
                Op.DIV -> a[i] / b[i]
            }
        }
        */
    }
}

data class LagExpr(
    val source: Expr,
    val periods: Int
) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries =
        ctx.eval(source)//.lag(periods)
}

enum class Op {
    ADD, SUB, MUL, DIV
}


typealias IndicatorFn = (EvalContext, List<Expr>) -> FloatSeries

class EvalContext(
    val table: OHLCVTable,
    val functions: Map<String, IndicatorFn>
) {
    val size: Int get() = table.size

    private val cache = mutableMapOf<Expr, FloatSeries>()

    // TODO sub expr caching
    // RSI14 - EMA(RSI14)
    fun eval(expr: Expr): FloatSeries =
        cache.getOrPut(expr) { expr.eval(this) }

    fun call(name: String, args: List<Expr>): FloatSeries {
        val fn = functions[name.uppercase()]
            ?: error("Unknown function: $name")

        return fn(this, args)
    }

    fun series(name: String): FloatSeries {
        return when (name.uppercase()) {
            "CLOSE" -> table.close
            "OPEN" -> table.open
            "HIGH" -> table.high
            "LOW" -> table.low
            "VOLUME" -> table.volume
            else -> error("Unknown series: $name")
        }
    }
}

val functions = mapOf<String, IndicatorFn>(
    "RSI" to { ctx, args ->
        val period = args[0].constantInt()
        RSI(period).eval(ctx.table)
    },

    "EMA" to { ctx, args ->
        val input = ctx.eval(args[0])
        val period = args[1].constantInt()
        ExpMovingAverage(period).eval(input)
    }
)

fun Expr.constantInt(): Int {
    return when (this) {
        is NumberExpr -> value.toInt()
        else -> error("Expected constant number")
    }
}
