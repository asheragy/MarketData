import data.DataSet
import data.TextDataRepository
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {

    fun getTestDataSet(): DataSet {
        val symbols = listOf("SPY", "XLE", "XLF", "XLK")
        val lists = symbols.map { symbol ->
            val fileName = "history_data/weekly/$symbol.txt"
            val data = resourceToString(fileName)
            TextDataRepository.stringToTable(data)
        }

        return DataSet.getNormalizedDataSet(lists.filter { it.symbol != "SPY" }, lists.first { it.symbol == "SPY" })
    }

    fun resourceToString(fileName: String): String {
        val classloader = Thread.currentThread().contextClassLoader
        val inputStream = classloader.getResourceAsStream(fileName)

        // Issue with getting resources in KMP project
        //if (inputStream == null)
        //    return fileToString("src\\jvmTest\\resources\\$fileName")

        val isr = InputStreamReader(inputStream)
        val br = BufferedReader(isr)
        val sb = StringBuffer()
        for(line in br.lines())
            sb.append(line + "\r\n")

        return sb.toString()
    }
}