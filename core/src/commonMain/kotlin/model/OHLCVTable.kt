package org.cerion.marketdata.core.model

import org.cerion.marketdata.core.arrays.FloatArray
import org.cerion.marketdata.core.arrays.toFloatArray
import org.cerion.marketdata.core.platform.KMPDate

open class OHLCVTable(
    val symbol: String,
    rows: List<OHLCVRow>,
    delegate: ArrayList<OHLCVRow> = ArrayList()
) : List<OHLCVRow> by delegate {

    val dates: Array<KMPDate> by lazy { map { it.date }.toTypedArray() }
    val open: FloatArray by lazy { map { it.open }.toFloatArray() }
    val high: FloatArray by lazy { map { it.high }.toFloatArray() }
    val low: FloatArray by lazy { map { it.low }.toFloatArray() }
    val close: FloatArray by lazy { map { it.close }.toFloatArray() }
    val volume: FloatArray by lazy { map { it.volume }.toFloatArray() }

    init {
        val sortedList = rows.sortedBy { it.date }
        delegate.addAll(sortedList)
    }
}