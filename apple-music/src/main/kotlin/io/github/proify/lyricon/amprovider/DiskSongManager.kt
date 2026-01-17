/*
 * Copyright 2026 Proify
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package io.github.proify.lyricon.amprovider

import android.content.Context
import io.github.proify.lyricon.amprovider.model.AppleSong
import io.github.proify.lyricon.provider.common.extensions.deflate
import io.github.proify.lyricon.provider.common.extensions.inflate
import io.github.proify.lyricon.provider.common.extensions.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import java.io.File

object DiskSongManager {
    private var baseDir: File? = null

    fun init(context: Context) {
        baseDir = File(context.filesDir, "lyricon/songs")
    }

    fun save(song: AppleSong): Boolean {
        val id = song.adamId
        if (id.isNullOrBlank()) return false
        val string = json.encodeToString(song)
        return runCatching {
            getFile(id)
                .also { it.parentFile?.mkdirs() }
                .writeBytes(
                    string
                        .toByteArray(Charsets.UTF_8)
                        .deflate()
                )
        }.isSuccess
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun load(id: String): AppleSong? {
        return runCatching {
            getFile(id)
                .takeIf { it.exists() }
                ?.readBytes()
                ?.inflate()
                ?.let {
                    json.decodeFromStream(
                        AppleSong.serializer(),
                        it.inputStream()
                    )
                }
        }.getOrNull()
    }

    fun hasCache(id: String): Boolean = getFile(id).exists()

    private fun getFile(id: String): File = File(baseDir, "$id.json.gz")
}