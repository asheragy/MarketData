package org.cerion.marketdata.core.model

class Symbol(val symbol: String, var name: String? = null, var exchange: String? = null) {

    val isValid: Boolean
        get() = (name != "N/A" && exchange != "N/A")
}
