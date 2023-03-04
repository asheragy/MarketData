package org.cerion.marketdata.core.platform

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

enum class DayOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
}


class KMPDate constructor(val _date: LocalDate) : Comparable<KMPDate> {

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val TODAY: KMPDate
            get() = KMPDate(LocalDate.now())

        fun parse(str: String): KMPDate {
            return KMPDate(LocalDate.parse(str))
        }
    }

    constructor(year: Int, month: Int, date: Int) : this(LocalDate.of(year, month, date))

    fun toISOString(): String = _date.toString() // YYYY-MM-DD
    override fun toString(): String = toISOString()

    // TODO equals should ignore time, depending how it was constructed the same dates may not return true
    override fun equals(other: Any?): Boolean {
        if (other is KMPDate)
            return _date.compareTo(other._date) == 0

        return false
    }

    override fun hashCode(): Int = _date.hashCode()
    override fun compareTo(other: KMPDate): Int = _date.compareTo(other._date)

    val dayOfWeek: DayOfWeek
        get() {
            return when (_date.dayOfWeek) {
                java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
                java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
                java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
                java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
                java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
                java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
                java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
                else -> throw RuntimeException()
            }
        }

    val year: Int
        get() = _date.year

    val dayOfMonth: Int
        get() = _date.dayOfMonth

    val month: Int // Returns 0-11 for Jan-Dec
        get() = _date.month.ordinal

    val jvmDate: LocalDate = _date

    fun add(days: Int): KMPDate {
        return KMPDate(_date.plusDays(days.toLong()))
    }

    fun diff(other: KMPDate): Int {
        return ChronoUnit.DAYS.between(other._date, this._date).toInt()
    }
}

fun LocalDate.toDate(): Date {
    return Date.from(
        this.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
    )
}

fun KMPDate.toDate(): Date {
    return this.jvmDate.toDate()
}