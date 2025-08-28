package org.cerion.stockcharts.ui.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.cerion.marketdata.core.charts.ChartColors
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.indicators.MACD
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.OHLCVTable
import org.cerion.marketdata.core.model.Symbol
import org.cerion.stockcharts.common.Event
import org.cerion.stockcharts.repository.CachedPriceListRepository
import org.cerion.stockcharts.repository.PreferenceRepository
import org.cerion.stockcharts.repository.PriceListRepository
import org.cerion.stockcharts.ui.charts.compose.ChartModel


class ChartsViewModel(
    private val repo: CachedPriceListRepository,
    private val sqlRepo: PriceListRepository,
    private val prefs: PreferenceRepository,
    private val colors: ChartColors) : ViewModel() {

    private val DefaultSymbol = Symbol("^GSPC", "S&P 500")

    private val _symbol = MutableLiveData(DefaultSymbol)
    val symbol: LiveData<Symbol>
        get() = _symbol

    private val _interval = MutableLiveData(Interval.DAILY)
    val interval: LiveData<Interval>
        get() = _interval

    private val _editChart = MutableLiveData<Event<StockChart>>()
    val editChart: LiveData<Event<StockChart>>
        get() = _editChart

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>>
        get() = _error

    private val DefaultCharts = mutableListOf(
            PriceChart(colors),
            VolumeChart(colors))

    /*
    private val DefaultChartsTest = mutableListOf(
            PriceChart(colors).apply {
                addOverlay(BollingerBands())
                addOverlay(SimpleMovingAverage())
                addOverlay(ExpMovingAverage())
            },
            VolumeChart(colors),
            IndicatorChart(MACD(), colors),
            PriceChart(colors).apply {
                addOverlay(ParabolicSAR())
            },
            VolumeChart(colors),
            IndicatorChart(AccumulationDistributionLine(), colors)
    )
     */

    private val _table = MutableStateFlow<OHLCVTable?>(null)
    val table: StateFlow<OHLCVTable?> = _table

    private val _charts = MutableStateFlow<List<ChartModel>>(emptyList())
    val charts: StateFlow<List<ChartModel>> = _charts

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean>
        get() = _busy

    /*
        TODO
        - ranges should be set on load since stock/crypto is different
        - result should be List<Pair<String, Int>>
        - Composable handles onClick and viewmodel doesn't need rangeSelect
     */
    private val rangeSelect = MutableLiveData<Event<Int>>()
    val ranges = _interval.map {
        when (it) {
            Interval.DAILY -> listOf("1M", "6M", "1Y", "MAX")
            Interval.WEEKLY -> listOf("3M", "1Y", "5Y", "MAX")
            Interval.MONTHLY -> listOf("1Y", "5Y", "10Y", "MAX")
            else -> listOf("3Y", "5Y", "10Y", "MAX")
        }
    }

    init {
        // Load saved charts
        val charts = prefs.getCharts(colors).toMutableList()
        if (charts.isEmpty())
            charts.addAll(DefaultCharts)

        _charts.value = charts.map { ChartModel(it) }
    }

    fun setInterval(interval: Interval) {
        if (_interval.value != interval) {
            _interval.value = interval

            refresh()
        }
    }

    fun load() {
        val lastSymbol = prefs.getSymbolHistory().lastOrNull()
        if (lastSymbol != null)
            _symbol.value = lastSymbol

        refresh()
    }

    private var saveSymbolAfterLoad = false
    fun load(symbol: Symbol) {
        _symbol.value = symbol

        saveSymbolAfterLoad = true
        refresh()
    }

    fun setRange(position: Int) {
        val range: Int = when(_interval.value) {
            // TODO daily is wrong with crypto
            // Logic should be built in charts
            Interval.DAILY -> when(position) {
                    0 -> 30 // 1 month
                    1 -> 125 // 6 month
                    2 -> 250 // year
                    else -> 0
                }
            Interval.WEEKLY -> when(position) {
                    0 -> 12 // 3 month
                    1 -> 50 // year
                    2 -> 50 * 5 // 5Y
                    else -> 0
                }
            Interval.MONTHLY -> when(position) {
                0 -> 12 // 1Y
                1 -> 12*5 // 5Y
                2 -> 12*10 // 10Y
                else -> 0
            }
            else -> 4 * when(position) { // Quarterly
                0 -> 3
                1 -> 5
                2 -> 10
                else -> 0
            }
        }

        rangeSelect.value = Event(range)
    }

    private var cleanupCache = true
    private fun refresh() {
        viewModelScope.launch {
            runBusy {
                // On the 2nd fetch of this app instance, cleanup database if needed
                if (table.value != null && cleanupCache) {
                    cleanupCache = false
                    sqlRepo.cleanupCache()
                }

                // TODO for debugging to catch unnecessary refreshes
                delay(200)
                val symbol = _symbol.value!!
                try {
                    _table.value = repo.get(symbol.symbol, _interval.value!!)

                    if (saveSymbolAfterLoad) {
                        prefs.addSymbolHistory(symbol)
                        saveSymbolAfterLoad = false
                    }
                } catch (e: Exception) {
                    _error.value = Event(e.message ?: "Failed to load $symbol")
                    _table.value = null
                }
            }
        }
    }

    private suspend fun runBusy(block: suspend () -> Unit) {
        try {
            _busy.value = true
            block()
        } finally {
            _busy.value = false
        }
    }

    fun editChart(chart: StockChart) {
        _editChart.value = Event(chart)
    }

    fun addPriceChart() {
        addChart(PriceChart(colors).apply {
            candleData = false
        })
    }

    fun addIndicatorChart() {
        val newChart = IndicatorChart(MACD(), colors)
        addChart(newChart)
        editChart(newChart)
    }

    fun addVolumeChart() {
        addChart(VolumeChart(colors))
    }

    fun removeChart(chart: StockChart) {
        _charts.value = _charts.value.filter { it.value != chart }
        saveCharts()
    }

    fun replaceChart(old: StockChart, new: StockChart) {
        _charts.value = _charts.value.map {
            if (it.value == old)
                ChartModel(new)
            else
                it
        }

        saveCharts()
    }

    fun clearCache() {
        viewModelScope.launch {
            runBusy {
                sqlRepo.clearCache()
            }
        }
    }

    private fun saveCharts() {
        prefs.saveCharts(_charts.value.map { it.value })
    }

    private fun addChart(chart: StockChart) {
        _charts.value += ChartModel(chart)
        saveCharts()
    }
}
