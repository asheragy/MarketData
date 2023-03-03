package org.cerion.marketdata.core

expect object Utils {
    fun readResourceFile(fileName: String): String

    fun runAsync(block: suspend () -> Unit)
}