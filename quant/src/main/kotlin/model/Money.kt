package model

import org.cerion.marketdata.core.model.OHLCVRow
import java.math.BigDecimal
import java.math.RoundingMode

data class Money(val amount: BigDecimal) {
    override fun toString(): String {
        val abs = amount.abs()
        return "${if (amount.signum() < 0) "-" else ""}$$abs"
    }

    operator fun minus(other: Money) = Money(amount - other.amount)
    operator fun plus(other: Money) = Money(amount + other.amount)

    operator fun compareTo(other: Money): Int {
        return amount.compareTo(other.amount)
    }

    fun maxShares(row: OHLCVRow): Double {
        return amount.toDouble() / row.close
    }

    // TODO this is weird or badly named
    fun percent(start: Money): String {
        val p = amount.toDouble() / start.amount.toDouble()
        return "%.2f".format(p * 100) + "%"
    }

    fun weighted(percent: Float): Money {
        return Money.buy(amount.toDouble() * percent)
    }

    companion object {
        fun buy(amount: Double): Money {
            return Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.FLOOR))
        }

        fun sell(amount: Double): Money {
            return Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.CEILING))
        }

        fun zero() = Money(BigDecimal.ZERO)

        fun of(amount: Double): Money {
            val value = BigDecimal(amount)
            if (value.scale() > 2)
                throw IllegalArgumentException("Amount must not exceed 2 digits")

            return Money(value.setScale(2))
        }
    }
}

fun Iterable<Money>.sum(): Money {
    return fold(Money.zero()) { total, money ->
        total + money
    }
}
