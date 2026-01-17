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

package io.github.proify.lyricon.amprovider

import android.media.MediaMetadata
import kotlinx.serialization.Serializable

object AppleMediaMetadata {
    private val metadataCache = object : LinkedHashMap<String, Metadata>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Metadata>?): Boolean =
            size > 100
    }

    fun putAndGet(metadata: MediaMetadata): Metadata? {
        val mediaId: String = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID) ?: return null
        val title: String = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist: String = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val duration: Long = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)

        val newMetadata = Metadata(mediaId, title, artist, duration)
        metadataCache[mediaId] = newMetadata
        return newMetadata
    }

    fun getMetadataById(mediaId: String): Metadata? = metadataCache[mediaId]

    @Serializable
    data class Metadata(
        val id: String,
        val title: String?,
        val artist: String?,
        val duration: Long
    )
}