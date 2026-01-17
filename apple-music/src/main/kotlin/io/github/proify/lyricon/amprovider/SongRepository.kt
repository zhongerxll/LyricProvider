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

import io.github.proify.lyricon.amprovider.parser.AppleSongParser
import io.github.proify.lyricon.amprovider.util.toSong
import io.github.proify.lyricon.lyric.model.Song

object SongRepository {

    /**
     * 根据 ID 获取歌曲
     * 策略：内存/磁盘缓存 -> 占位符
     */
    fun getSong(id: String): Song {
        // 1. 尝试从磁盘缓存读取
        val cache = DiskSongManager.load(id)
        if (cache != null) {
            return cache.toSong()
        }

        // 2. 缓存未命中，从 Metadata 生成占位符（只有标题/歌手，无歌词）
        val metadata = AppleMediaMetadata.getMetadataById(id)
        return Song(id, metadata?.title, metadata?.artist)
    }

    /**
     * 保存解析好的歌曲到磁盘
     */
    fun saveSong(nativeSongObj: Any): Song? {
        val song = AppleSongParser.parser(nativeSongObj)
        if (song.adamId.isNullOrBlank()) {
            return null
        }
        DiskSongManager.save(song)
        return song.toSong()
    }
}