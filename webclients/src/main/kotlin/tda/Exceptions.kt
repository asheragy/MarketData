package org.cerion.marketdata.webclients.tda

import okhttp3.Response

class RequestException(response: Response) : Exception() {

    val code = response.code
    override val message = response.message
    override fun toString(): String = "$code $message"
}