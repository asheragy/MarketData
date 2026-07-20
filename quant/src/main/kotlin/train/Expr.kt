package train

import data.SectorETFDef
import data.TextDataRepository
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.ExpMovingAverage
import org.cerion.marketdata.core.series.FloatSeries


fun main() {
    val dataSource = TextDataRepository()
    val dataSet = dataSource.get(SectorETFDef())
    val index = dataSet.index!!

    val rsi: Expr = CallExpr("RSI", 2)
    val rsi2: Expr = CallExpr("RSI", 2)
    val ctx = EvalContext(index)

    ctx.eval(rsi)
    ctx.eval(rsi2)

    println(rsi)
}

sealed interface Expr {
    fun eval(ctx: EvalContext): FloatSeries

    operator fun minus(other: Expr) = BinaryExpr(this, Op.SUB, other)
    operator fun div(other: Expr) = BinaryExpr(this, Op.DIV, other)
}

data class NumberExpr(val value: Number) : Expr {
    override fun eval(ctx: EvalContext) = TODO() //FloatSeries(ctx.size)

    override fun toString(): String {
        return value.toString()
    }
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

    constructor(name: String, vararg args: Number) : this(name, args.map { NumberExpr(it) })
    constructor(name: String, vararg args: Expr) : this(name, args.asList())

    override fun toString(): String {
        return "$name(${args.joinToString(", ")})"
    }
}

data class BinaryExpr(
    val left: Expr,
    val op: Op,
    val right: Expr
) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries {
        val a = ctx.eval(left)
        val b = ctx.eval(right)

        return when(op) {
            Op.ADD -> TODO()
            Op.SUB -> a.subtract(b)
            Op.MUL -> TODO()
            Op.DIV -> a.divide(b)
        }
    }

    override fun toString(): String {
        val str = when (op) {
            Op.ADD -> "+"
            Op.SUB -> "-"
            Op.MUL -> "*"
            Op.DIV -> "/"
        }

        return "$left $str $right"
    }
}

// TODO add good unit test for this one
data class LagExpr(
    val source: Expr,
    val periods: Int
) : Expr {
    override fun eval(ctx: EvalContext) = ctx.eval(source).offset(periods)

    override fun toString(): String {
        return "$source[${-periods}]"
    }
}

enum class Op {
    ADD, SUB, MUL, DIV
}


typealias IndicatorFn = (EvalContext, List<Expr>) -> FloatSeries

class EvalContext(
    val table: OHLCVTable
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
