package org.cerion.stockcharts.ui.charts

import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.MenuCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import org.cerion.marketdata.core.charts.IndicatorChart
import org.cerion.marketdata.core.charts.PriceChart
import org.cerion.marketdata.core.charts.StockChart
import org.cerion.marketdata.core.charts.VolumeChart
import org.cerion.marketdata.core.model.Interval
import org.cerion.marketdata.core.model.Symbol
import org.cerion.stockcharts.R
import org.cerion.stockcharts.appCompatActivity
import org.cerion.stockcharts.common.SymbolSearchView
import org.cerion.stockcharts.database.getDatabase
import org.cerion.stockcharts.databinding.FragmentComposeChartsBinding
import org.cerion.stockcharts.ui.AppTheme
import org.cerion.stockcharts.ui.charts.compose.IndicatorChart
import org.cerion.stockcharts.ui.charts.compose.ViewportPayload
import org.koin.androidx.viewmodel.ext.android.viewModel

class ComposeChartsFragment : Fragment() {

    private val viewModel: ChartsViewModel by viewModel()
    private lateinit var binding: FragmentComposeChartsBinding
    private lateinit var adapter: ChartListAdapter

    companion object {
        fun newInstance(symbol: String): ComposeChartsFragment {
            val fragment = ComposeChartsFragment()
            val args = Bundle()
            args.putString("symbol", symbol)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentComposeChartsBinding.inflate(inflater, container, false)

        appCompatActivity?.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        val chartListener = object : StockChartListener {
            override fun onClick(chart: StockChart) {
                viewModel.editChart(chart)
            }

            override fun onViewPortChange(matrix: Matrix) {
                syncCharts(matrix)
            }
        }

        adapter = ChartListAdapter(requireContext(), chartListener)
        //binding.recyclerView.adapter = adapter

        val chartsChangedObserver = Observer<Any?> {
            var intervals = 0
            viewModel.rangeSelect.value?.getContentIfNotHandled()?.also {
                intervals = it
            }

            adapter.setCharts(viewModel.charts.value!!, viewModel.table.value, intervals)
        }

        viewModel.busy.observe(viewLifecycleOwner) {
            binding.loadingBar.visibility = if(it) View.VISIBLE else View.GONE
        }

        viewModel.symbol.observe(viewLifecycleOwner) {
            appCompatActivity?.supportActionBar?.title = it.symbol
            binding.title.text = it.name
        }

        // Intervals
        binding.interval.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                viewModel.interval.value = Interval.values()[position]
            }
        }

        viewModel.ranges.observe(viewLifecycleOwner) {
            it.forEachIndexed { index, label ->
                val chip = binding.ranges[index] as Chip
                chip.text = label
            }
        }

        binding.ranges.children.forEachIndexed { index, view ->
            view as Chip
            view.setOnClickListener {
                viewModel.setRange(index)
            }
        }

        viewModel.editChart.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { chart ->
                val fm = requireActivity().supportFragmentManager
                val dialog = EditChartDialog.newInstance(chart, viewModel)
                dialog.show(fm, "editDialog")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.charts.observe(viewLifecycleOwner, chartsChangedObserver)
        viewModel.table.observe(viewLifecycleOwner, chartsChangedObserver)
        viewModel.rangeSelect.observe(viewLifecycleOwner, chartsChangedObserver)

        if (savedInstanceState == null) {
            if (arguments != null) {
                val str = ChartsFragmentArgs.fromBundle(requireArguments()).symbol
                val symbol = Symbol(str)
                viewModel.load(symbol)
            }
            else
                viewModel.load()
        }

        binding.composeView.setContent {
            val charts by viewModel.charts.observeAsState(listOf())
            val table by viewModel.table.observeAsState(null)
            var viewPort by remember { mutableStateOf<ViewportPayload?>(null) }

            AppTheme {
                Surface(color = MaterialTheme.colorScheme.surface) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(charts) { chartModel ->
                            val listener = object : StockChartListener {
                                override fun onClick(chart: StockChart) {

                                }

                                override fun onViewPortChange(matrix: Matrix) {
                                    viewPort = ViewportPayload(matrix)
                                }
                            }

                            if(table == null)
                                Text(text = "Loading...")
                            else
                                when(chartModel) {
                                    is PriceChart -> org.cerion.stockcharts.ui.charts.compose.PriceChart(chartModel, table!!, listener, viewPort)
                                    is VolumeChart -> org.cerion.stockcharts.ui.charts.compose.VolumeChart(chartModel, table!!, listener, viewPort)
                                    is IndicatorChart -> IndicatorChart(chartModel, table!!, listener, viewPort)
                                }
                        }
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

    private val _mainVals = FloatArray(9)
    private fun syncCharts(matrix: Matrix) {
        /*
        matrix.getValues(_mainVals)
        for(view in binding.recyclerView.children) {
            adapter.syncMatrix(matrix, _mainVals, binding.recyclerView.getChildViewHolder(view) as ChartListAdapter.ViewHolder)
        }

         */
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
}