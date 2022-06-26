package org.cerion.marketdata.core.charts

import org.cerion.marketdata.core.model.OHLCVTable

class CandleDataSet(private val table: OHLCVTable, override val label: String) : IDataSet {

    override val color: Int = 0 // Special case data set, ignore color value
    override val size: Int = table.size - 1
    override val lineType: LineType = LineType.CANDLE

    fun getHigh(pos: Int): Float = table.high[pos + 1]
    fun getLow(pos: Int): Float = table.low[pos + 1]
    fun getOpen(pos: Int): Float = table.open[pos + 1]
    fun getClose(pos: Int): Float = table.close[pos + 1]
}
