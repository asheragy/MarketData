package org.cerion.marketdata.webclients

import java.util.*

data class OAuthResponse(val accessToken: String, val refreshToken: String?, val expiresIn: Int, val refreshExpiresIn: Int?) {
    val expireDate = Date(Date().time + (expiresIn * 1000))
}