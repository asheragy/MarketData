package train

enum class Align {
    LEFT,
    CENTER,
    RIGHT
}

data class TableColumn<T>(
    val title: String,
    val align: Align = Align.RIGHT,
    val value: (T) -> String
)

object Table {

    fun <T> print(
        rows: List<T>,
        columns: List<TableColumn<T>>,
        separator: String = " | ",
        printDivider: Boolean = true
    ) {
        if (columns.isEmpty()) return

        val renderedRows: List<List<String>> = rows.map { row ->
            columns.map { column -> column.value(row) }
        }

        val widths: List<Int> = columns.mapIndexed { columnIndex, column ->
            val maxValueWidth = renderedRows.maxOfOrNull { row ->
                row[columnIndex].length
            } ?: 0

            maxOf(column.title.length, maxValueWidth)
        }

        val header = columns
            .mapIndexed { index, column ->
                column.title.align(widths[index], Align.CENTER)
            }
            .joinToString(separator)

        println(header)

        if (printDivider) {
            val divider = widths
                .joinToString(separator) { "-".repeat(it) }

            println(divider)
        }

        renderedRows.forEach { renderedRow ->
            val line = renderedRow
                .mapIndexed { index, value ->
                    value.align(widths[index], columns[index].align)
                }
                .joinToString(separator)

            println(line)
        }
    }
}

private fun String.align(width: Int, align: Align): String {
    return when (align) {
        Align.LEFT -> this.padEnd(width)
        Align.CENTER -> this.padCenter(width)
        Align.RIGHT -> this.padStart(width)
    }
}

private fun String.padCenter(width: Int, padChar: Char = ' '): String {
    if (this.length >= width) return this

    val totalPadding = width - this.length
    val leftPadding = totalPadding / 2
    val rightPadding = totalPadding - leftPadding

    return padChar.toString().repeat(leftPadding) +
            this +
            padChar.toString().repeat(rightPadding)
}

fun Double.decimal2(): String =
    "%.2f".format(this)

fun Float.decimal2(): String =
    this.toDouble().decimal2()

fun Double.percent2(): String =
    "${"%.2f".format(this)}%"

fun Float.percent2(): String =
    this.toDouble().percent2()

fun Double.money2(): String =
    if (this < 0) {
        "-$${"%.2f".format(-this)}"
    } else {
        "$${"%.2f".format(this)}"
    }

fun Float.money2(): String =
    this.toDouble().money2()
