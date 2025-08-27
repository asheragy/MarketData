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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
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
import org.cerion.stockcharts.ui.charts.compose.IntervalDropDownMenu
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
        setHasOptionsMenu(true)


        viewModel.busy.observe(viewLifecycleOwner) {
            binding.loadingBar.visibility = if(it) View.VISIBLE else View.GONE
        }

        viewModel.symbol.observe(viewLifecycleOwner) {
            appCompatActivity?.supportActionBar?.title = it.symbol
            binding.title.text = it.name ?: it.symbol
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
            val charts by viewModel.chartModels.observeAsState(listOf())
            val ranges by viewModel.ranges.observeAsState(listOf())
            val interval by viewModel.interval.observeAsState(Interval.DAILY)
            val table by viewModel.table.observeAsState(null)

            AppTheme {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ranges.take(4).forEach { label ->
                                    AssistChip(
                                        onClick = {
                                            val index = ranges.indexOf(label)
                                            if (index >= 0) {
                                                Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show()
                                                viewModel.setRange(index)
                                            }
                                        },
                                        label = { Text(label) }
                                    )
                                }
                            }

                            IntervalDropDownMenu(interval) {
                                viewModel.setInterval(it)
                            }
                        }
                        ChartList(charts, table, onClick = {
                            onEditChart(it)
                        })
                    }
                }
            }
        }

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.charts_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SymbolSearchView

        searchView.setOnSymbolClickListener(object : SymbolSearchView.OnSymbolClickListener {
            override fun onClick(symbol: Symbol) {
                viewModel.load(symbol)
                menuItem.collapseActionView()
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_indicator -> viewModel.addIndicatorChart()
            R.id.add_price -> viewModel.addPriceChart()
            R.id.add_volume -> viewModel.addVolumeChart()
            R.id.clear_cache -> viewModel.clearCache()
            R.id.stats -> showStats()
            else -> return super.onContextItemSelected(item)
        }

        return true
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