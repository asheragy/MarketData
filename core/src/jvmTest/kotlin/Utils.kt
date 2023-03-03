package org.cerion.marketdata.core

import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

actual object Utils {

    actual fun readResourceFile(fileName: String): String {
        return resourceToString(fileName)
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
}

