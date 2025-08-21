package org.cerion.stockcharts.ui.crypto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

data class QuoteRowUi(
    val name: String,
    val price: Double,
    val change1: Double,
    val change2: Double,
    val change3: Double
)

val decimalFormat = DecimalFormat("#0.00")

@Composable
private fun CryptoRow(
    quote: QuoteRowUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                onClick()
            }
    ) {
        // Left column: Name and Price
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quote.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = decimalFormat.format(quote.price),
                textAlign = TextAlign.Center
            )
        }

        // Right section: Three changes
        Row(
            modifier = Modifier
                .weight(2f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = decimalFormat.format(quote.change1),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = decimalFormat.format(quote.change2),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = decimalFormat.format(quote.change3),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CryptoList(
    quotes: List<QuoteRowUi>,
    onClick: (QuoteRowUi) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(quotes) { quote ->
            CryptoRow(quote) {
                onClick(quote)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCryptoList() {
    val sampleData = listOf(
        QuoteRowUi("Ethereum", 1000.01, -0.67, 2.34, 9.34),
        QuoteRowUi("Bitcoin", 25000.50, 1.45, -0.32, 5.20),
        QuoteRowUi("Solana", 45.67, 0.15, 0.80, -1.20)
    )

    CryptoList(sampleData, onClick = {})
}
