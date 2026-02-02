/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.spotifyprovider.xposed

import io.github.proify.lyricon.spotifyprovider.xposed.api.NoFoundLyricException
import io.github.proify.lyricon.spotifyprovider.xposed.api.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

object Downloader {
    private const val MAX_RETRY_ATTEMPTS = 3
    private const val INITIAL_RETRY_INTERVAL_MS = 100L

    private val downloadingIds = ConcurrentHashMap.newKeySet<String>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun download(
        id: String,
        authorization: String,
        clientToken: String,
        downloadCallback: DownloadCallback
    ): Job? {
        if (!downloadingIds.add(id)) return null

        return scope.launch {
            try {
                repeat(MAX_RETRY_ATTEMPTS + 1) { attempt ->
                    try {
                        val response = SpotifyApi.fetchLyricResponse(id, authorization, clientToken)
                        downloadCallback.onDownloadFinished(id, response)
                        return@launch
                    } catch (e: Exception) {
                        if (e is NoFoundLyricException || !isActive || attempt >= MAX_RETRY_ATTEMPTS) {
                            downloadCallback.onDownloadFailed(id, e)
                            return@launch
                        }
                        val delayTime = INITIAL_RETRY_INTERVAL_MS * 2.0.pow(attempt).toLong()
                        delay(delayTime)
                    }
                }
            } finally {
                downloadingIds.remove(id)
            }
        }
    }
}