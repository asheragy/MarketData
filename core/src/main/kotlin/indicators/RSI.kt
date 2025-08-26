package org.cerion.marketdata.core.indicators

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.ValueArray
import org.cerion.marketdata.core.functions.types.Indicator
import org.cerion.marketdata.core.model.OHLCVTable
import kotlin.math.abs

class RSI(period: Int = 14) : IndicatorBase(Indicator.RSI, period) {

    override val name: String = "RSI"

    override fun eval(table: OHLCVTable): FloatArray {
        return rsi(table, getInt(0))
    }

    private fun rsi(table: OHLCVTable, period: Int): FloatArray {
        val arr = table.close
        val size = arr.size
        val result = FloatArray(size)
        if (size == 0)
            return result

        /*
        for(int i = period; i < size(); i++)
        {
            float gain = 0;
            float loss = 0;

	        for(int j = i-period+1; j <= i; j++)
	        {
	            float diff = get(j).adjClose - get(j-1).adjClose;
	            if(diff > 0)
	                gain += diff;
	            else
	                loss += -diff;
	        }

	        float avgGain = gain / period;
	        float avgLoss = loss / period;
	        float RS = avgGain / avgLoss;

	        if(avgLoss == 0)
	        	get(i).rsi_values[pos] = 100;
	        else if(avgGain == 0)
	        	get(i).rsi_values[pos] = 0;
	        else
	        	get(i).rsi_values[pos] = 100 - (100/(1+RS));

	        //System.out.println( get(i-1).rsi_values[pos] );
        }
        */

        //Smoothed RSI, gain/loss calculated slightly different than above
        /*
        for(int i = 1; i < (period+1); i++)
        {
            float diff = arr.mVal[i] - arr.mVal[i-1];
            if(diff > 0)
                gain += diff;
            else
                loss += -diff;
        }

        float avgGain = gain / period;
        float avgLoss = loss / period;
        float RS = avgGain / avgLoss;
        result.mVal[period] = 100 - (100/(1+RS));
        */

        //System.out.println(get(period).date + "\t" + get(period).rsi);

        // Start in middle range with averages as the first difference
        result[0] = 50f
        var avgGain = abs(arr[1] - arr[0])
        var avgLoss = avgGain

        for (i in 1 until size) {
            var gain = 0f
            var loss = 0f
            val p = ValueArray.maxPeriod(i, period)

            val diff = arr[i] - arr[i - 1]
            if (diff > 0)
                gain = diff
            else
                loss = -diff

            avgGain = (avgGain * (p - 1) + gain) / p
            avgLoss = (avgLoss * (p - 1) + loss) / p

            if (avgLoss == 0f)
                result[i] = 100f
            else if (avgGain == 0f)
                result[i] = 0f
            else {
                val RS = avgGain / avgLoss
                result[i] = 100 - 100 / (1 + RS)
            }
        }

        return result
    }
}
