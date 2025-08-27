package org.cerion.stockcharts.ui.charts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fakes.FakePreferenceRepository
import fakes.FakePriceHistoryDataSource
import fakes.FakePriceListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.cerion.marketdata.core.charts.ChartColors
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.indicators.RSI
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.Symbol
import org.cerion.stockcharts.repository.CachedPriceListRepository
import org.cerion.stockcharts.repository.DefaultPriceHistoryDates
import org.cerion.stockcharts.repository.PreferenceRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChartsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var _viewModel: ChartsViewModel
    private lateinit var _prefs: PreferenceRepository
    private lateinit var _cachedRepo: CachedPriceListRepository

    @Before
    fun init() {
        val priceListRepository = FakePriceListRepository()
        val priceHistory = FakePriceHistoryDataSource()
        val dates = DefaultPriceHistoryDates()
        _cachedRepo = CachedPriceListRepository(priceListRepository, priceHistory, dates, mainDispatcherRule.dispatcher)
        _prefs = FakePreferenceRepository()

        _viewModel = ChartsViewModel(_cachedRepo, FakePriceListRepository(), _prefs, ChartColors())
    }

    @Test
    fun chartsViewModel_defaults() {
        assertEquals(Interval.DAILY, _viewModel._interval.value)
        assertEquals("^GSPC", _viewModel.symbol.value!!.symbol)
        assertFalse(_viewModel.busy.value!!)
        assertEquals(2, _viewModel.charts.value!!.size)
        assertTrue(_viewModel.charts.value!![0] is PriceChart)
        assertTrue(_viewModel.charts.value!![1] is VolumeChart)
    }

    @Test
    fun chartsViewModel_loadsFromPrefs() {
        _prefs.saveCharts(listOf(VolumeChart()))
        _viewModel = ChartsViewModel(_cachedRepo, FakePriceListRepository(), _prefs, ChartColors())

        assertEquals(1, _viewModel.charts.value!!.size)
        assertTrue(_viewModel.charts.value!![0] is VolumeChart)
    }

    @Test
    fun chartsViewModel_savesToPrefs() {
        _viewModel.addPriceChart()
        _viewModel.addIndicatorChart()
        _viewModel.addVolumeChart()

        assertEquals(5, _prefs.getCharts(ChartColors()).size)
    }

    @Test
    fun chartsViewModel_removeChart() {
        val chartToRemove = _viewModel.charts.value!![0]
        _viewModel.removeChart(chartToRemove)

        assertEquals(1, _viewModel.charts.value!!.size)
        assertTrue(_viewModel.charts.value!![0] is VolumeChart)
    }

    @Test
    fun chartsViewModel_replace() {
        val chartToReplace = _viewModel.charts.value!![0]
        val chartToAdd = IndicatorChart(RSI(14))

        _viewModel.replaceChart(chartToReplace, chartToAdd)
        assertEquals(2, _viewModel.charts.value!!.size)
        assertTrue(_viewModel.charts.value!![0] is IndicatorChart)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chartsViewModel_loadsFromEndOfHistory() = runTest {
        _prefs.addSymbolHistory(Symbol("SPY"))
        _prefs.addSymbolHistory(Symbol("AAPL"))

        _viewModel.load()
        assertEquals("AAPL", _viewModel.symbol.value!!.symbol)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chartsViewModel_invalidSymbolError() = runTest {
        _viewModel.load(Symbol("<ex>")) // Throws exception
        advanceUntilIdle()
        assertTrue(_viewModel.error.value!!.getContentIfNotHandled()!!.isNotEmpty())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chartsViewModel_invalidSymbolNotSaved() = runTest {
        _viewModel.load(Symbol("<ex>")) // Throws exception
        advanceUntilIdle()
        assertEquals(0, _prefs.getSymbolHistory().size)

        _viewModel.load(Symbol("GOOG"))
        advanceUntilIdle()
        assertEquals(1, _prefs.getSymbolHistory().size)
    }
}