package org.cerion.stockcharts.ui.crypto

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import org.cerion.stockcharts.ui.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class PortfolioFragment : Fragment() {

    private val viewModel: CryptoViewModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val positions1 by viewModel.positions.observeAsState(emptyList())
                val total1 = positions1.sumOf { it.totalValue }
                val slices1 = positions1.map { PieSlice(it.symbol, 100 * (it.totalValue / total1).toFloat(), if(it.cash) LIGHT_GREEN else null) }

                val positions2 by viewModel.positionsAlts.observeAsState(emptyList())
                val total2 = positions2.sumOf { it.totalValue }
                val slices2 = positions2.map { PieSlice(it.symbol, 100 * (it.totalValue / total2).toFloat(), if(it.cash) LIGHT_GREEN else null) }

                val total by viewModel.total.observeAsState(0.0)

                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        Column(modifier = Modifier.fillMaxHeight()) {
                            Text(text = "$" + DecimalFormat("#.##").format(total),
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold)
                            SimplePieChart(slices1, modifier = Modifier.weight(1f)
                                .fillMaxWidth())
                            SimplePieChart(slices2, modifier = Modifier.weight(1f)
                                .fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
