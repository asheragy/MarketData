package org.cerion.marketdata.core.overlays

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.functions.types.Overlay
import kotlin.math.abs

class KAMA(er: Int, fast: Int, slow: Int) : OverlayBase<FloatArray>(Overlay.KAMA, er, fast, slow) {

    constructor() : this(10, 2, 30)

    override val name: String = "Adaptive Moving Average"

    override fun eval(arr: FloatArray): FloatArray {
        val p1 = getInt(0)
        val p2 = getInt(1)
        val p3 = getInt(2)


        val result = FloatArray(arr.size)
        result[0] = arr[0]

        //p1 Efficiency Ratio (ER)
        //p2 Fastest ExpMovingAverage
        //p3 Slowest ExpMovingAverage

        //ER = Change/Volatility
        //Change = ABS(Close - Close (X periods ago))
        //Volatility = SumX(ABS(Close - Prior Close))
        for (i in 1 until arr.size) {
            var start = i - p1
            if (start < 1)
                start = 1

            val change = abs(arr[i] - arr[start])
            var volatility = 0f

            //SumX
            for (j in start..i)
                volatility += abs(arr[j] - arr[j - 1])

            val ER = change / volatility

            //SC = [ER x (fastest SC - slowest SC) + slowest SC]^2
            //SC = [ER x (2/(2+1) - 2/(30+1)) + 2/(2+1)]^2
            val fastEMA = (2.0 / (p2 + 1.0)).toFloat()
            val slowEMA = (2.0 / (p3 + 1.0)).toFloat()
            var SC = ER * (fastEMA - slowEMA) + slowEMA
            SC *= SC

            //Current KAMA = Prior KAMA + SC x (Price - Prior KAMA)
            val prior = result[i - 1]
            result[i] = prior + SC * (arr[i] - prior)
        }

        return result
    }
}
