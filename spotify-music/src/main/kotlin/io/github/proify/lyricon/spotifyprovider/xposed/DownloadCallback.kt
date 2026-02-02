/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.spotifyprovider.xposed

import io.github.proify.lyricon.spotifyprovider.xposed.api.response.LyricResponse

interface DownloadCallback {
    fun onDownloadFinished(id: String, response: LyricResponse)
    fun onDownloadFailed(id: String, e: Exception)
}