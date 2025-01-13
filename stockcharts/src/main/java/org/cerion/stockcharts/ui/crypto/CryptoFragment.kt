package org.cerion.stockcharts.ui.crypto

import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import org.cerion.stockcharts.R
import org.cerion.stockcharts.databinding.FragmentCryptoBinding
import org.json.JSONObject


class CryptoFragment : Fragment() {

    private lateinit var binding: FragmentCryptoBinding
    private val viewModel = CryptoViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCryptoBinding.inflate(inflater, container, false)

        val adapter = CryptoListAdapter(object : CryptoListListener {
            override fun onClick(symbol: String) {
                // TODO add back when api data works
                //val action = HomeFragmentDirections.actionFragmentHomeToChartsFragment(symbol)
                //findNavController().navigate(action)

                val i = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://finance.yahoo.com/quote/$symbol"))
                startActivity(i)
            }
        })

        val fileStream = requireContext().resources.openRawResource(R.raw.crypto)
        val positionFile = JSONObject(fileStream.bufferedReader().use { it.readText() })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load(positionFile)
        }

        viewModel.rows.observe(viewLifecycleOwner) {
            adapter.setRows(it)
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

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))


        viewModel.load(positionFile)

        return binding.root
    }
}
