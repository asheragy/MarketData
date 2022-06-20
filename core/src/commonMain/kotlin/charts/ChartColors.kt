package org.cerion.marketdata.core.charts

open class ChartColors {

    // Basic colors
    var primary = 0 // (white or black) depending on theme
    var primaryBlue = 0
    var volumneBlue = 0
    var positiveGreen = 0
    var negativeRed = 0
    var primaryPurple = 0 // Used for common indicators such as RSI / MACD

    // Secondary and overlay colors (no blue/red/green/purple)
    var orange = 0
    var teal = 0
    var yellow = 0
    var secondaryBlue = 0
    var secondaryRed = 0
    var secondaryGreen = 0

    fun getOverlayColor(index: Int): Int {
        // Allow for now since unit tests don't always set color
        // assert(orange != purple) { "colors must be unique" }

        // Order of rotating overlay colors
        return when(index % 6) {
            0 -> orange
            1 -> teal
            2 -> secondaryGreen
            3 -> yellow
            4 -> secondaryBlue
            5 -> secondaryRed
            else -> throw IndexOutOfBoundsException()
        }
    }
}