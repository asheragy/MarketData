package data

import org.cerion.marketdata.core.PriceList
import org.cerion.marketdata.core.model.OHLCVRow
import org.cerion.marketdata.core.platform.KMPDate
import org.cerion.marketdata.webclients.FetchInterval
import org.cerion.marketdata.webclients.yahoo.YahooFinance
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate

class TextDataRepository: DataRepository {

    private val api = YahooFinance.instance
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun upsert(data: DataDef) {
        if (data.index != null)
            insert(data.index!!, data.interval, data.minLength)

        data.symbols.forEach { symbol ->
            insert(symbol, data.interval, data.minLength)
        }
    }

    override fun get(data: DataDef): DataSet {
        val lists = data.symbols.map { symbol -> getList(symbol, data.interval) }
        val index = if (data.index != null) getList(data.index!!, data.interval) else null

        return DataSet(lists, index)
    }

    private fun getList(symbol: String, interval: FetchInterval): PriceList {
        val fileName = "./history_data/${interval.toString().lowercase()}/$symbol.txt"
        val sTable = File(fileName).readText()

        val prices = mutableListOf<OHLCVRow>()

        val lines: Array<String> = sTable.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val symbol = lines[0]
        for (line in lines) {
            val fields = line.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (fields.size == 6) {
                try {
                    val date = LocalDate.parse(fields[0])
                    val open = fields[1].toFloat()
                    val high = fields[2].toFloat()
                    val low = fields[3].toFloat()
                    val close = fields[4].toFloat()
                    val volume = fields[5].toLong(10)
                    val p = OHLCVRow(KMPDate(date), open, high, low, close, volume.toFloat())
                    prices.add(p)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val list = PriceList(symbol, prices)
        //check(!(list.interval !== interval)) { "intervals do not match" }

        return list
    }

    private fun insert(symbol: String, interval: FetchInterval, minLength: Int) {
        val prices = api.getPrices(symbol, interval, null)
        if (prices.size < minLength)
            throw RuntimeException("Length ${prices.size} less than $minLength on $symbol")

        saveFile(prices, symbol, interval)
    }

    private fun getTableString(symbol: String, list: List<OHLCVRow>): String {
        var table = "Symbol=$symbol\r\n"

        for (p in list)
            table += java.lang.String.format(
                "%s|%s|%s|%s|%s|%s\r\n",
                p.date.toISOString(),
                p.open,
                p.high,
                p.low,
                p.close,
                p.volume.toLong())

        return table
    }

    fun saveFile(prices: List<OHLCVRow>, symbol: String, interval: FetchInterval) {
        val fileName = "./history_data/${interval.toString().lowercase()}/$symbol.txt"
        val file = File(fileName)
        if (!file.exists())
            file.createNewFile()

        val content = getTableString(symbol, prices)
        file.writeText(content)
    }
}