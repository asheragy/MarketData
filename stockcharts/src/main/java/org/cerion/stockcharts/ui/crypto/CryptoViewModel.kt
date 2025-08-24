package org.cerion.stockcharts.ui.crypto

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cerion.marketdata.webclients.coingecko.CoinGecko
import org.cerion.stockcharts.R
import org.json.JSONObject


data class CryptoRow(val name: String,
                     val symbol: String) {
    var quote: CoinGecko.DetailedQuote? = null
}

data class Position(val symbol: String, val totalValue: Double, val cash: Boolean = false)

class CryptoViewModel(context: Application) : ViewModel() {
    private val positionFile: JSONObject
    private val api = CoinGecko()

    private val _rows = MutableStateFlow<List<CryptoRow>>(emptyList())
    val rows: StateFlow<List<CryptoRow>>
        get() = _rows

    private val _positions = MutableStateFlow<List<PieSlice>>(emptyList())
    val positions: StateFlow<List<PieSlice>>
        get() = _positions

    private val _positionsAlts = MutableStateFlow<List<PieSlice>>(emptyList())
    val positionsAlts: StateFlow<List<PieSlice>>
        get() = _positionsAlts

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double>
        get() = _total

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean>
        get() = _busy

    private val mappings = mapOf(
        "bitcoin" to CryptoRow("Bitcoin","BTC-USD"),
        "bitcoin-cash" to CryptoRow("Bitcoin Cash", "BCH-USD"),
        //"matic-network" to CryptoRow("Polygon/Matic", "MATIC-USD"),
        "algorand" to CryptoRow("Algorand","ALGO-USD"),
        "ethereum" to CryptoRow("Ethereum","ETH-USD"),
        "solana" to CryptoRow("Solana","SOL-USD"),
        //"binancecoin" to CryptoRow("BNB","BNB-USD"),
        "litecoin" to CryptoRow("Litecoin","LTC-USD"),
        "cardano" to CryptoRow("Cardano", "ADA-USD"),
        "dogecoin" to CryptoRow("Dogecoin", "DOGE-USD"),
        "hedera-hashgraph" to CryptoRow("Hedera", "HBAR-USD"),
        "ripple" to CryptoRow("XRP", "XRP-USD")
    )

    init {
        val fileStream = context.resources.openRawResource(R.raw.crypto)
        positionFile = JSONObject(fileStream.bufferedReader().use { it.readText() })
        load()
    }

    fun load() {
        viewModelScope.launch {
            _busy.value = true
            val result = withContext(Dispatchers.IO) {
                val ids = mappings.keys.toList()
                val response = api.getDetailedQuotes(ids)

                response.forEach {
                    mappings[it.id]?.apply {
                        this.quote = it
                    }
                }

                mappings.values.toList()
            }

            // Quotes
            _rows.value = result.sortedBy { it.name }

            // Positions
            val positions = result.map {
                val quantity = if (positionFile.has(it.quote?.id))
                    positionFile.getDouble(it.quote!!.id)
                else
                    0.0

                val pricePerShare = it.quote?.price ?: 0.0
                Position(it.symbol.substringBefore("-"), quantity * pricePerShare)

            }.filter { it.totalValue > 0 }

            val mainCoins = listOf("BTC", "ETH", "SOL")
            val alts = positions.filter { x -> !mainCoins.contains(x.symbol)}
            val altPosition = Position("Alts", alts.sumOf { x -> x.totalValue })

            val cashPosition = Position("Cash", 1505.0, true)
            val mainPositions = positions.filter { x -> mainCoins.contains(x.symbol) }.plus(altPosition).plus(
                cashPosition)

            _total.value = mainPositions.sumOf { it.totalValue }
            _positions.value = mainPositions.toPieSlice()
            _positionsAlts.value = alts.toPieSlice()
            _busy.value = false
        }
    }
}

fun List<Position>.toPieSlice(): List<PieSlice> {
    val total = this.sumOf { it.totalValue }
    return this.map { PieSlice(it.symbol, 100 * (it.totalValue / total).toFloat(), if(it.cash) LIGHT_GREEN else null) }
}