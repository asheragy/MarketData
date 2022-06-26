package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.toFloatArray
import org.cerion.marketdata.core.platform.KMPDate

class OHLCVTable(
    val symbol: String,
    rows: List<OHLCVRow>,
    delegate: ArrayList<OHLCVRow> = ArrayList()
) : List<OHLCVRow> by delegate {

    val dates: Array<KMPDate> by lazy { map { it.date }.toTypedArray() }
    val opens: FloatArray by lazy { map { it.open }.toFloatArray() }
    val highs: FloatArray by lazy { map { it.high }.toFloatArray() }
    val lows: FloatArray by lazy { map { it.low }.toFloatArray() }
    val closes: FloatArray by lazy { map { it.close }.toFloatArray() }
    val volumes: FloatArray by lazy { map { it.volume }.toFloatArray() }

    init {
        val sortedList = rows.sortedBy { it.date }
        delegate.addAll(sortedList)
    }
}