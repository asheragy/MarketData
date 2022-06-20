package org.cerion.marketdata.core.platform

import java.util.*

actual class KMPTimeStamp(private val date: Date) : Comparable<KMPTimeStamp> {
    override fun compareTo(other: KMPTimeStamp): Int = date.compareTo(other.date)

    constructor() : this(Date())

    actual val time: Long
        get() = date.time
}