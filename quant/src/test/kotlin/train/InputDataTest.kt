package train

import org.cerion.marketdata.core.model.OHLCVTable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InputDataTest {

    @Test
    fun allInputsEvaluateToFloatSeries() {
        val table = OHLCVTable.generateSeries(320)
        val index = OHLCVTable.generateSeries(320)
        val ctx = EvalContext(table, index)

        Inputs.forEach { input ->
            val series = ctx.eval(input.expr)

            assertEquals(table.size, series.size, input.expr.toString())
        }
    }
}
