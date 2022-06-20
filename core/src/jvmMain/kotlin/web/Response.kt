package org.cerion.marketdata.core.web

class Response {
    var code: Int = 0
    var result: String = ""
    var headers: Map<String, List<String>> = mutableMapOf()
}
