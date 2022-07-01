package org.cerion.marketdata.webclients

class Response {
    var code: Int = 0
    var result: String = ""
    var headers: Map<String, List<String>> = mutableMapOf()
}
