package org.cerion.stockcharts.ui.crypto

import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.cerion.stockcharts.R
import org.cerion.stockcharts.databinding.FragmentCryptoBinding
import org.cerion.stockcharts.ui.AppTheme
import org.cerion.stockcharts.ui.HomeFragmentDirections
import org.json.JSONObject


class CryptoFragment : Fragment() {

    private lateinit var binding: FragmentCryptoBinding
    private val viewModel = CryptoViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCryptoBinding.inflate(inflater, container, false)

        val fileStream = requireContext().resources.openRawResource(R.raw.crypto)
        val positionFile = JSONObject(fileStream.bufferedReader().use { it.readText() })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load(positionFile)
        }

        viewModel.rows.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.positions.observe(viewLifecycleOwner) {
            binding.chartTotal.setPositions(it)
        }

        viewModel.positionsAlts.observe(viewLifecycleOwner) {
            binding.chartAlts.setPositions(it)
        }

        viewModel.total.observe(viewLifecycleOwner) {
            binding.totalAmount.text = "$" + DecimalFormat("#.##").format(it)
        }

        viewModel.load(positionFile)

        binding.composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent {

            val rows by viewModel.rows.observeAsState(emptyList())
            val converted = rows.map { QuoteRowUi(it.name, it.quote!!.price, it.quote!!.changeDay, it.quote!!.changeWeek, it.quote!!.changeMonth) }

            AppTheme {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    CryptoList(converted) { clicked ->
                        val symbol = rows.first { clicked.name == it.name }.symbol
                        navigate(symbol)
                    }
                }

            }
        }

        return binding.root
    }

    private fun navigate(symbol: String) {
        if (true) {
            val assetId = symbol.replace("-USD", "")
            val action = HomeFragmentDirections.actionFragmentHomeToChartsFragment(assetId)
            findNavController().navigate(action)
        } else {
            val i = Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://finance.yahoo.com/quote/$symbol"))
            startActivity(i)
        }
    }
}
