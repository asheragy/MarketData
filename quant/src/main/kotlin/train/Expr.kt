package train

import data.SectorETFDef
import data.TextDataRepository
import org.cerion.marketdata.core.functions.IFunction
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.overlays.OverlayBase
import org.cerion.marketdata.core.series.FloatSeries


fun main() {
    val dataSource = TextDataRepository()
    val dataSet = dataSource.get(SectorETFDef())
    val index = dataSet.index!!

    val rsi: Expr = FuncExpr(RSI(2))
    val rsi2: Expr = FuncExpr(RSI(2))
    val ctx = EvalContext(index, index)

    ctx.eval(rsi)
    ctx.eval(rsi2)

    println(rsi)
}

sealed interface Expr {
    fun eval(ctx: EvalContext): FloatSeries

    operator fun minus(other: Expr) = BinaryExpr(this, Op.SUB, other)
    operator fun div(other: Expr) = BinaryExpr(this, Op.DIV, other)
}

data class TargetExpr(val index: Boolean = false): Expr {
    override fun eval(ctx: EvalContext): FloatSeries {
        TODO("Not for direct eval")
    }

    override fun toString() = if (index) "index" else "stock"
}

data class FieldExpr(val name: String) : Expr {
    override fun eval(ctx: EvalContext): FloatSeries =
        ctx.series(name)
}

data class CustomExpr(val name: String, val eval: (EvalContext) -> FloatSeries) : Expr {
    override fun eval(ctx: EvalContext) = eval.invoke(ctx)
    override fun toString() = name
}

// TODO index/input should be replaced by an Expr that represents what data to run the function on
data class FuncExpr private constructor(val function: IFunction, val index: Boolean, val input: Expr? = null) : Expr {
    constructor(function: IFunction) : this(function, false, null)
    constructor(index: Boolean, function: IFunction, ) : this(function, index, null)
    constructor(input: Expr, function: IFunction) : this(function, false, input)

    override fun eval(ctx: EvalContext): FloatSeries {
        val table = if (index) ctx.index else ctx.table

        if (input != null)  {
            if (function is OverlayBase<*>) {
                val inputEval =  input.eval(ctx)
                val result = function.eval(inputEval)
                if (result is FloatSeries) {
                    return result
                }

                throw RuntimeException("Function result is not FloatSeries")
            }

            throw RuntimeException("Function does not implement eval(FloatSeries)")
        }

        val result = function.eval(table)
        if (result is FloatSeries) {
            return result
        }

        throw RuntimeException("Function result is not FloatSeries")
    }

    override fun toString(): String {
        val params = function.params.map { it.toString() }.toMutableList()
        if (index)
            params.add(0, "index")
        if (input != null)
            params.add(0, input.toString())
        return "${this.function.javaClass.simpleName}(" + params.joinToString(", ") + ")"
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

class EvalContext(
    val table: OHLCVTable,
    val index: OHLCVTable
) {
    val size: Int get() = table.size

    private val cache = mutableMapOf<Expr, FloatSeries>()

    // TODO sub expr caching
    // RSI14 - EMA(RSI14)
    fun eval(expr: Expr): FloatSeries =
        cache.getOrPut(expr) { expr.eval(this) }

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
