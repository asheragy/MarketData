package org.cerion.stockcharts.ui.crypto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cerion.marketdata.webclients.coingecko.CoinGecko
import org.json.JSONObject
import kotlin.math.max


data class CryptoRow(val name: String,
                     val symbol: String) {
    var quote: CoinGecko.DetailedQuote? = null
}

interface Position {
    val symbol: String
    val quantity: Double
    val pricePerShare: Double
    val totalValue: Double
    val cash: Boolean
}

data class CryptoPosition(val row: CryptoRow, override val quantity: Double) : Position {
    override val symbol = row.symbol.substringBefore("-")
    override val pricePerShare = row.quote?.price ?: 0.0
    override val totalValue = quantity * pricePerShare
    override val cash = false
}

data class CashPosition(override val quantity: Double, val positive: Boolean) : Position {
    override val symbol = if(positive) "Sell" else "Buy"
    override val pricePerShare = 1.0
    override val totalValue = pricePerShare * quantity
    override val cash = true
}

data class AltsPosition(override val quantity: Double) : Position {
    override val symbol = "Alts"
    override val pricePerShare = 1.0
    override val totalValue = pricePerShare * quantity
    override val cash = false
}

class CryptoViewModel : ViewModel() {

    private val api = CoinGecko()

    private val _rows = MutableLiveData<List<CryptoRow>>()
    val rows: LiveData<List<CryptoRow>>
        get() = _rows

    private val _positions = MutableLiveData<List<Position>>()
    val positions: LiveData<List<Position>>
        get() = _positions

    private val _positionsAlts = MutableLiveData<List<Position>>()
    val positionsAlts: LiveData<List<Position>>
        get() = _positionsAlts

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double>
        get() = _total

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

    fun load(positionFile: JSONObject) {
        viewModelScope.launch {
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

            _rows.value = result.sortedBy { it.name }

            val positions = result.map {
                val amount = if (positionFile.has(it.quote?.id))
                    positionFile.getDouble(it.quote!!.id)
                else
                    0.0

                CryptoPosition(it, amount)
            }.filter { it.quantity > 0 }

            _total.value = positions.sumOf { it.totalValue }

            val mainCoins = listOf("BTC-USD", "ETH-USD", "SOL-USD",
                //"LTC-USD"
            )

            val alts = positions.filter { x -> !mainCoins.contains(x.row.symbol)}
            val altPosition = AltsPosition(alts.sumOf { x -> x.totalValue })

            var mainPositions = positions.filter { x -> mainCoins.contains(x.row.symbol) }.plus(altPosition)

            // Profit/Loss
            val total = positions.map { it.totalValue }.sum()
            val t = 1_600_000
            val g = 1.1 / 100
            val min = t * g * 0.85
            val max = t * g * 1.15
            if (total < min)
                mainPositions = mainPositions.plus(CashPosition(max(0.0, min - total), false))
            else if (total > max)
                mainPositions = mainPositions.plus(CashPosition(max(0.0, total - max), true))

            _positions.value = mainPositions
            _positionsAlts.value = alts
        }
    }

}