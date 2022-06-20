package org.cerion.marketdata.core

import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlin.js.Promise

actual object Utils {
    actual fun runAsync(block: suspend () -> Unit): dynamic = GlobalScope.promise {
        block()
    }

    actual suspend fun readResourceFileAsync(fileName: String): Deferred<String> {
        return getText(fileName).asDeferred()
    }

    private fun getText(fileName: String): Promise<String> {
        return window.fetch(fileName).then {
            it.text()
        }.then {
            it
        }
    }
}