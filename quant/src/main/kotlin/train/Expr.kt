package train

/*
sealed interface Expr {
    fun eval(ctx: EvalContext): DoubleArray
}

data class NumberExpr(val value: Double) : Expr {
    override fun eval(ctx: EvalContext): DoubleArray =
        DoubleArray(ctx.size) { value }
}

data class FieldExpr(val name: String) : Expr {
    override fun eval(ctx: EvalContext): DoubleArray =
        ctx.series(name)
}

data class CallExpr(
    val name: String,
    val args: List<Expr>
) : Expr {
    override fun eval(ctx: EvalContext): DoubleArray =
        ctx.call(name, args)
}

data class BinaryExpr(
    val left: Expr,
    val op: Op,
    val right: Expr
) : Expr {
    override fun eval(ctx: EvalContext): DoubleArray {
        val a = ctx.eval(left)
        val b = ctx.eval(right)

        return DoubleArray(ctx.size) { i ->
            when (op) {
                Op.ADD -> a[i] + b[i]
                Op.SUB -> a[i] - b[i]
                Op.MUL -> a[i] * b[i]
                Op.DIV -> a[i] / b[i]
            }
        }
    }
}

data class LagExpr(
    val source: Expr,
    val periods: Int
) : Expr {
    override fun eval(ctx: EvalContext): DoubleArray =
        ctx.eval(source).lag(periods)
}

enum class Op {
    ADD, SUB, MUL, DIV
}

 */


/*
typealias IndicatorFn = (EvalContext, List<Expr>) -> DoubleArray

class EvalContext(
    val table: OHLCVTable,
    val functions: Map<String, IndicatorFn>
) {
    val size: Int get() = table.size

    private val cache = mutableMapOf<Expr, DoubleArray>()

    fun eval(expr: Expr): DoubleArray =
        cache.getOrPut(expr) { expr.eval(this) }

    fun call(name: String, args: List<Expr>): DoubleArray {
        val fn = functions[name.uppercase()]
            ?: error("Unknown function: $name")

        return fn(this, args)
    }

    fun series(name: String): DoubleArray {
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
 */
