package org.cerion.stockcharts.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.core.view.MenuCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.Symbol
import org.cerion.stockcharts.R
import org.cerion.stockcharts.appCompatActivity
import org.cerion.stockcharts.common.SymbolSearchView
import org.cerion.stockcharts.database.getDatabase
import org.cerion.stockcharts.databinding.FragmentChartsBinding
import org.cerion.stockcharts.ui.AppTheme
import org.cerion.stockcharts.ui.charts.compose.ChartList
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChartsFragment : Fragment() {

    private val viewModel: ChartsViewModel by viewModel()
    private lateinit var binding: FragmentChartsBinding

    companion object {
        fun newInstance(symbol: String): ChartsFragment {
            val fragment = ChartsFragment()
            val args = Bundle()
            args.putString("symbol", symbol)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChartsBinding.inflate(inflater, container, false)

        appCompatActivity?.setSupportActionBar(binding.toolbar)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.charts_menu, menu)
                MenuCompat.setGroupDividerEnabled(menu, true)

                val menuItem = menu.findItem(R.id.action_search)
                val searchView = menuItem.actionView as? SymbolSearchView

                searchView?.setOnSymbolClickListener(object : SymbolSearchView.OnSymbolClickListener {
                    override fun onClick(symbol: Symbol) {
                        viewModel.load(symbol)
                        menuItem.collapseActionView()
                    }
                })
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.add_indicator -> {
                        viewModel.addIndicatorChart()
                        true
                    }
                    R.id.add_price -> {
                        viewModel.addPriceChart()
                        true
                    }
                    R.id.add_volume -> {
                        viewModel.addVolumeChart()
                        true
                    }
                    R.id.clear_cache -> {
                        viewModel.clearCache()
                        true
                    }
                    R.id.stats -> {
                        showStats()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.symbol.observe(viewLifecycleOwner) {
            appCompatActivity?.supportActionBar?.title = it.symbol
            //binding.title.text = it.name ?: it.symbol
        }

        viewModel.editChart.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { chart ->
                onEditChart(chart)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        if (savedInstanceState == null) {
            if (arguments != null) {
                val str = ChartsFragmentArgs.fromBundle(requireArguments()).symbol
                val symbol = Symbol(str)
                viewModel.load(symbol)
            }
            else
                viewModel.load()
        }

        /*
        TODO observe rangeSelect event
        this was the old logic to adjust the range

        chartView as BarLineChartBase<*>
        if (intervals != 0) {
            val end = table!!.close.size.toFloat()
            val start = kotlin.math.max(0.0f, end - intervals.toFloat())

            chartView.setVisibleXRangeMaximum(intervals.toFloat())
            chartView.moveViewToX(end - start - 1)
            chartView.setVisibleXRangeMaximum(table!!.close.size.toFloat()) // Workaround to make viewport manually adjustable again
        }
         */

        binding.composeView.setContent {
            val nestedScroll = rememberNestedScrollInteropConnection()
            val charts by viewModel.chartModels.observeAsState(listOf())
            val ranges by viewModel.ranges.observeAsState(listOf())
            val interval by viewModel.interval.observeAsState(Interval.DAILY)
            val table by viewModel.table.observeAsState(null)
            val loading by viewModel.busy.observeAsState(false)

            AppTheme {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Column {
                        ChartList(
                            charts,
                            table,
                            scrollConnection = nestedScroll,
                            loading = loading,
                            interval = interval,
                            ranges = ranges,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        return binding.root
    }

    // Debug only
    private fun showStats() {
        val db = getDatabase(requireContext())
        val name = getDatabase(requireContext()).openHelper.databaseName
        val file = requireContext().getDatabasePath(name)
        val sizeInKb = file.length() / 1024

        //Log.i("Main", "Size before compact: ${file.length()}")
        val lists = db.priceListDao.getAll()

        Toast.makeText(requireContext(), "${lists.size} lists with size ${sizeInKb}kb", Toast.LENGTH_LONG).show()
    }

    private fun onEditChart(chart: StockChart) {
        val dialog = EditChartDialog.newInstance(chart, viewModel)
        dialog.show(childFragmentManager, "editDialog")
    }
}