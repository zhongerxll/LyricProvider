/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.spotifyprovider.xposed.api

import io.github.proify.lyricon.spotifyprovider.xposed.api.response.LyricResponse
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.util.Locale

object SpotifyApi {

    private val jsonParser: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        prettyPrint = true
        coerceInputValues = true
    }

    @Throws(Exception::class)
    fun fetchLyricResponse(
        id: String,
        authorization: String,
        clientToken: String
    ): LyricResponse {
        val rawJson = fetchRawLyric(id, authorization, clientToken)
        val response = jsonParser.decodeFromString<LyricResponse>(rawJson)
        return response
    }

    @Throws(Exception::class)
    fun fetchRawLyric(
        id: String,
        authorization: String,
        clientToken: String
    ): String {
        val urlString =
            "https://spclient.wg.spotify.com/color-lyrics/v2/track/$id"
        val url = URI.create(urlString).toURL()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000

            setRequestProperty("accept", "application/json")
            setRequestProperty("accept-language", Locale.getDefault().toLanguageTag())
            setRequestProperty("app-platform", "WebPlayer")

            setRequestProperty("authorization", authorization)
            setRequestProperty("client-token", clientToken)
        }

        return try {
            when (val responseCode = connection.responseCode) {
                HttpURLConnection.HTTP_OK ->
                    connection.inputStream.bufferedReader().use { it.readText() }

                HttpURLConnection.HTTP_INTERNAL_ERROR ->
                    throw NoFoundLyricException(id, "Lyric not found for $id")

                else -> throw IOException("HTTP Error: $responseCode, Message: ${connection.responseMessage}")

            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection.disconnect()
        }
    }
}