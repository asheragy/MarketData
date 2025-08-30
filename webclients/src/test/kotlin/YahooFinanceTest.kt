

import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.yahoo.YahooFinance
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate

class YahooFinanceTest {
    private val api = YahooFinance.instance
    private val oneYearAgo = LocalDate.now().minusYears(1)

    @Test
    fun daily() {
        val prices = api.getPrices("OHI", FetchInterval.DAILY, oneYearAgo)
        assertTrue(prices.size > 0)
    }

    @Test
    fun bitcoin() {
        val prices = api.getPrices("BTC-USD", FetchInterval.DAILY, oneYearAgo)
        assertTrue(prices.size > 0)
    }

   @Test
   @Disabled
    fun coins() {
       val start = LocalDate.now().minusDays(90)
       val coins = listOf("BCH", "ALGO", "ETH", "SOL", "LTC", "ADA", "DOGE", "HBAR", "XRP")

       coins.forEach {
           val prices = api.getPrices("$it-USD", FetchInterval.DAILY, start)
           assertTrue(prices.isNotEmpty())
       }
    }

    @Test
    fun prices() {
        for(i in 0..19) {
            println(i)

            val list =
                    when(i) {
                        0 -> api.getPrices("OHI", FetchInterval.DAILY, oneYearAgo)
                        1 -> api.getPrices("OHI", FetchInterval.WEEKLY, oneYearAgo)
                        2 -> api.getPrices("OHI", FetchInterval.MONTHLY, oneYearAgo)

                        3 -> api.getPrices("^GSPC", FetchInterval.DAILY, oneYearAgo)
                        4 -> api.getPrices("^GSPC", FetchInterval.WEEKLY, oneYearAgo)
                        5 -> api.getPrices("^GSPC", FetchInterval.MONTHLY, oneYearAgo)

                        6 -> api.getPrices("FNMIX", FetchInterval.DAILY, oneYearAgo)
                        7 -> api.getPrices("FNMIX", FetchInterval.WEEKLY, oneYearAgo)
                        8 -> api.getPrices("FNMIX", FetchInterval.MONTHLY, oneYearAgo)

                        9 -> api.getPrices("AAPL", FetchInterval.DAILY, oneYearAgo)
                        10 -> api.getPrices("AAPL", FetchInterval.WEEKLY, oneYearAgo)
                        11 -> api.getPrices("AAPL", FetchInterval.MONTHLY, oneYearAgo)

                        /*
                        12 -> {
                            val date = GregorianCalendar(2012, 2, 1).getTime();
                            list = PriceList ("SPY", api.getPrices("SPY", Interval.DAILY, date));
                        }
                        */

                        else -> null
                    }

            if (i < 12) {
                if (list == null || list.isEmpty())
                    throw Exception("priceHistory failed at i = $i")

                // TODO should check based on interval so range is closer to expected size
                assertTrue(list.size > 10 || list.size < 260, "Incorrect list size ${list.size}")
            }

            Thread.sleep(500)
        }

        /*
        PriceList L =
        L = api.getPrices("OHI", Interval.WEEKLY, 100);
        L = api.getPrices("OHI", Interval.MONTHLY, 100);

        List<Dividend> divs = api.getDividends("OHI");

        System.out.println("Success = " + L.size());
        */
    }
}