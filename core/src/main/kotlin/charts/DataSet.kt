package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.arrays.BandArray
import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.MACDArray
import org.cerion.marketdata.core.arrays.PairArray

class DataSet(private val values: FloatArray, override val label: String, override var color: Int) : IDataSet, Iterable<Float> {

    override var lineType = LineType.LINE
    override val size: Int = values.size - 1

    @Deprecated("Use iterator") // Not sure if this can completely go
    operator fun get(pos: Int): Float = values[pos + 1]

    override fun iterator(): Iterator<Float> {
        return object : Iterator<Float> {
            private var index = 1 // Always ignore first element in FloatArray
            override fun hasNext(): Boolean = index < values.size
            override fun next(): Float = values[index++]
        }
    }
}

fun FloatArray.toDataSet(label: String, color: Int) = DataSet(this, label, color)

fun BandArray.getDataSets(labelUpper: String, labelLower: String, color: Int): List<DataSet> {
    return listOf(DataSet(upper, labelUpper, color), DataSet(lower, labelLower, color))
}

fun PairArray.getDataSets(labelPos: String, labelNeg: String, colorPos: Int, colorNeg: Int): List<DataSet> {
    return listOf(DataSet(positive, labelPos, colorPos), DataSet(negative, labelNeg, colorNeg))
}

fun MACDArray.getDataSets(labelMACD: String, labelSignal: String, labelHist: String,
                          colorMACD: Int, colorSignal: Int, colorHist: Int): List<DataSet> {

    val signal = FloatArray(size)
    val hist = FloatArray(size)

    // TODO make function to get signal/hist arrays directly
    for (i in 0 until size) {
        signal[i] = signal(i)
        hist[i] = hist(i)
    }

    return listOf(DataSet(this, labelMACD, colorMACD),
            DataSet(signal, labelSignal, colorSignal),
            DataSet(hist, labelHist, colorHist))
}