package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.PriceOverlay
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.max
import kotlin.math.min

class ParabolicSAR(step: Double, maxStep: Double) : PriceOverlayBase(PriceOverlay.PSAR, step, maxStep) {

    constructor() : this(0.02, 0.2)

    override fun eval(table: OHLCVTable): FloatArray {
        return parabolicSAR(table, getFloat(0), getFloat(1))
    }

    override val name: String = "Parabolic SAR"

    private fun parabolicSAR(table: OHLCVTable, step: Float, max: Float): FloatArray {
        val result = FloatArray(table.size)
        val close = table.close
        var start = 1

        while (close[start - 1] == close[start])
            start++

        when {
            close[start - 1] > close[start] -> sarFalling(table, result, start, table.high[start - 1], step, max)
            close[start - 1] < close[start] -> sarRising(table, result, start, table.low[start - 1], step, max)
            else -> println("error")
        } //above should fix this

        return result
    }

    private fun sarRising(table: OHLCVTable, result: FloatArray, start: Int, sar_start: Float, step: Float, max: Float) {
        result[start] = sar_start

        var alpha = step
        var sar = sar_start
        var ep = table.high[start]

        for (i in start + 1 until table.size) {
            ep = max(ep, table.high[i])
            if (ep == table.high[i] && alpha + step <= max)
                alpha += step

            if (ep - sar < 0)
                println("sarRising() error")

            sar += alpha * (ep - sar)

            if (sar > table.low[i]) {
                sarFalling(table, result, i, ep, step, max)
                return
            }

            result[i] = sar
        }

    }

    private fun sarFalling(table: OHLCVTable, result: FloatArray, start: Int, sar_start: Float, step: Float, max: Float) {
        //System.out.println(p.date + "\t" + sar_start + "\tFalling");
        result[start] = sar_start

        var alpha = step
        var sar = sar_start
        var ep = table.low[start]

        for (i in start + 1 until table.size) {
            ep = min(ep, table.low[i])
            if (ep == table.low[i] && alpha + step <= max)
                alpha += step

            if (sar - ep < 0)
                println("sarFalling error")

            sar -= alpha * (sar - ep)
            if (sar < table.high[i]) {
                sarRising(table, result, i, ep, step, max)
                return
            }

            result[i] = sar
        }
    }
}
