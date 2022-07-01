package org.cerion.marketdata.webclients.tda

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.cerion.marketdata.webclients.OAuthResponse
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder

// TODO made this generic class after adding another OAuth client

internal class TDAmeritradeAuth(private val consumerKey: String, private val redirectUri: String) {
    private val client = OkHttpClient()
    val authUrlEncoded: String
        get() {
            // TODO encode key too
            return "https://auth.tdameritrade.com/auth?response_type=code&redirect_uri=${URLEncoder.encode(redirectUri)}&client_id=$consumerKey@AMER.OAUTHAP"
        }

    fun authorize(code: String): OAuthResponse {
        val postBody: RequestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("access_type", "offline")
                .add("client_id", consumerKey)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build()

        val request = Request.Builder()
                .url("https://api.tdameritrade.com/v1/oauth2/token")
                .post(postBody)
                .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        println(body)

        val json = JSONObject(body)
        return OAuthResponse(
                json["access_token"] as String,
                json["refresh_token"] as String,
                json["expires_in"] as Int,
                json["refresh_token_expires_in"] as Int)
    }

    fun refreshAuth(refreshToken: String): OAuthResponse {
        val postBody: RequestBody = FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("client_id", consumerKey)
                .add("refresh_token", refreshToken)
                .build()

        val request = Request.Builder()
                .url("https://api.tdameritrade.com/v1/oauth2/token")
                .post(postBody)
                .build()

        val response = client.newCall(request).execute()
        if (response.code != HttpURLConnection.HTTP_OK)
            throw RequestException(response)

        val body = response.body?.string()
        println(body)

        val json = JSONObject(body)
        return OAuthResponse(
                json["access_token"] as String,
                null,
                json["expires_in"] as Int,
                null)
    }
}