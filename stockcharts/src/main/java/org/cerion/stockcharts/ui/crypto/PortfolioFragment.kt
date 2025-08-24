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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.cerion.stockcharts.ui.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class PortfolioFragment : Fragment() {

    private val viewModel: CryptoViewModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val positions1 by viewModel.positions.collectAsStateWithLifecycle()
                val positions2 by viewModel.positionsAlts.collectAsStateWithLifecycle()
                val total by viewModel.total.collectAsStateWithLifecycle()

                AppTheme {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        Column(modifier = Modifier.fillMaxHeight()) {
                            Text(text = "$" + DecimalFormat("#.##").format(total),
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold)
                            SimplePieChart(positions1, modifier = Modifier.weight(1f)
                                .fillMaxWidth())
                            SimplePieChart(positions2, modifier = Modifier.weight(1f)
                                .fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
