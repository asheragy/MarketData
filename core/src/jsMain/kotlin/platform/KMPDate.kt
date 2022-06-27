package org.cerion.marketdata.core.platform

import kotlin.js.Date

const val MS_PER_DAY = 1000 * 60 * 60 * 24

actual class KMPDate actual constructor(year: Int, month: Int, date: Int) : Comparable<KMPDate> {

    private var _date: Date = Date(Date.UTC(year, month - 1, date))
    constructor(date: Date) : this(date.getUTCFullYear(), date.getUTCMonth() + 1, date.getUTCDate())

    override fun compareTo(other: KMPDate): Int = _date.getTime().compareTo(other._date.getTime())

    actual override fun equals(other: Any?): Boolean {
        if (other is KMPDate)
            return this.compareTo(other) == 0

        return false
    }

    actual fun toISOString(): String = _date.toISOString().substring(0, 10)

    actual val time: Long
        get() = TODO("Not yet implemented")

    actual val dayOfWeek: DayOfWeek
        get() {
            return when (_date.getUTCDay()) {
                0 -> DayOfWeek.SUNDAY
                1 -> DayOfWeek.MONDAY
                2 -> DayOfWeek.TUESDAY
                3 -> DayOfWeek.WEDNESDAY
                4 -> DayOfWeek.THURSDAY
                5 -> DayOfWeek.FRIDAY
                6 -> DayOfWeek.SATURDAY
                else -> throw RuntimeException()
            }
        }

    actual val year: Int
        get() = _date.getUTCFullYear()

    actual val dayOfMonth: Int
        get() = _date.getUTCDate()

    actual val month: Int
        get() = _date.getUTCMonth()

    actual fun add(days: Int): KMPDate {
        val date = Date(_date.toString())
        date.asDynamic().setUTCDate(date.getUTCDate() + days)
        return KMPDate(date)
    }

    actual fun diff(other: KMPDate): Int {
        // TODO is this necessary?
        val utc1 = Date.UTC(_date.getUTCFullYear(), _date.getUTCMonth(), _date.getUTCDate())
        val utc2 = Date.UTC(other._date.getUTCFullYear(), other._date.getUTCMonth(), other._date.getUTCDate())

        return ((utc1 - utc2) / MS_PER_DAY).toInt()
    }

    actual companion object {
        actual val TODAY: KMPDate
            get() = KMPDate(Date())

        actual fun parse(str: String): KMPDate {
            val date = Date(Date.parse(str))
            return KMPDate(date.toUTC())
        }
    }
}

private fun Date.toUTC(): Date = Date(Date.UTC(getUTCFullYear(), getUTCMonth(), getUTCDate(), getUTCHours(), getUTCMinutes()))