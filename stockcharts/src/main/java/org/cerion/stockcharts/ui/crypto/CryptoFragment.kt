package org.cerion.stockcharts.ui.crypto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import org.cerion.stockcharts.ui.AppTheme
import org.cerion.stockcharts.ui.HomeFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel


class CryptoFragment : Fragment() {

    private val viewModel: CryptoViewModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val rows by viewModel.rows.collectAsStateWithLifecycle()
                val converted = rows.map { QuoteRowUi(it.name, it.quote!!.price, it.quote!!.changeDay, it.quote!!.changeWeek, it.quote!!.changeMonth) }
                val refreshing by viewModel.busy.collectAsStateWithLifecycle()
                val total by viewModel.total.collectAsStateWithLifecycle()

                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.surface) {

                        PullToRefreshBox(
                            isRefreshing = refreshing,
                            onRefresh = {
                                viewModel.load()
                            },
                        ) {
                            CryptoList(converted, total) { clicked ->
                                val symbol = rows.first { clicked.name == it.name }.symbol
                                navigate(symbol)
                            }
                        }
                    }

                }
            }
        }
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
