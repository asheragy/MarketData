package org.cerion.marketdata.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.cerion.marketdata.core.model.Dividend
import org.cerion.marketdata.core.platform.KMPDate
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

actual object Utils {

    actual suspend fun readResourceFileAsync(fileName: String): Deferred<String> {
        return CompletableDeferred(resourceToString(fileName))
    }

    actual fun runAsync(block: suspend () -> Unit) = runBlocking {
        block()
    }

    fun resourceToString(fileName: String): String {
        val classloader = Thread.currentThread().contextClassLoader
        val inputStream = classloader.getResourceAsStream(fileName)

        // Issue with getting resources in KMP project
        if (inputStream == null)
            return fileToString("src\\jvmTest\\resources\\$fileName")

        val isr = InputStreamReader(inputStream)
        val br = BufferedReader(isr)
        val sb = StringBuffer()
        for(line in br.lines())
            sb.append(line + "\r\n")

        return sb.toString()
    }

    private fun fileToString(fileName: String): String {
        return File(fileName).readText(Charsets.UTF_8)
    }

    fun getDividends(vararg values: Float): List<Dividend> {
        var date = KMPDate.TODAY
        val result = ArrayList<Dividend>()

        for (v in values) {
            val d = Dividend(date, v)
            result.add(d)

            date = date.add(-1)
        }

        return result
    }

    fun getDate(daysAgo: Int) = KMPDate.TODAY.add(-daysAgo)
}

