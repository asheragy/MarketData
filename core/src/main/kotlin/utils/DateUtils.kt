package utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun LocalDate.diff(other: LocalDate): Int {
    return ChronoUnit.DAYS.between(other, this).toInt()
}

fun LocalDate.toDate(): Date {
    return Date.from(
        this.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
    )
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

