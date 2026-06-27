package data

data class Sector(val etf: String, val index: String, val name: String)

val Sectors = listOf(
    Sector("XLB", "^YH101", "Materials"),
    Sector("XLC", "^YH308", "Communication Services"),
    Sector("XLY", "^YH102", "Consumer Discretionary"),
    Sector("XLP", "^YH205", "Consumer Staples"),
    Sector("XLE", "^YH309", "Energy"),
    Sector("XLF", "^YH103", "Financials"),
    Sector("XLV", "^YH206", "Healthcare"),
    Sector("XLI", "^YH310", "Industrials"),
    Sector("XLRE", "^YH104", "Real Estate"),
    Sector("XLK", "^YH311", "Technology"),
    Sector("XLU", "^YH207", "Utilities"),
)

val SectorIndexToETF = Sectors.associate { it.index to it.etf }
