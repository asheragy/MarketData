package org.cerion.marketdata.core.platform

@Suppress("NO_ACTUAL_FOR_EXPECT") // TODO temporary for KMP bug
expect class KMPTimeStamp : Comparable<KMPTimeStamp> {
    val time: Long
}