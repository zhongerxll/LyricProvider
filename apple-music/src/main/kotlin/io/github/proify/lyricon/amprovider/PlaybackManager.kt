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

import com.highcapable.yukihookapi.hook.log.YLog
import io.github.proify.lyricon.lyric.model.Song
import io.github.proify.lyricon.provider.remote.RemotePlayer

object PlaybackManager {
    private var player: RemotePlayer? = null
    private var lyricRequester: LyricRequester? = null

    // 状态追踪
    private var currentSongId: String? = null
    private var lastSong: Song? = null

    fun init(remotePlayer: RemotePlayer, requester: LyricRequester) {
        this.player = remotePlayer
        this.lyricRequester = requester
    }

    /**
     * 当系统切歌或 Metadata 变化时调用
     */
    fun onSongChanged(newId: String?) {
        if (newId == null) {
            currentSongId = null
            setSong(null)
            return
        }

        // 避免重复处理同一首歌
        if (newId == currentSongId) return
        currentSongId = newId

        YLog.debug("PlaybackManager: Song changed to $newId")

        // 1. 立即设置歌曲（可能是完整版，也可能是占位版）
        val song = SongRepository.getSong(newId)
        setSong(song)

        // 2. 检查是否需要下载歌词
        if (song.lyrics.isNullOrEmpty()) {
            lyricRequester?.requestDownload(newId)
        } else {
            YLog.debug("PlaybackManager: Song $newId has lyrics, skipping download.")
        }
    }

    /**
     * 当 Hook 捕获到歌词构建完成时调用
     */
    fun onLyricsBuilt(nativeSongObj: Any) {
        val song = SongRepository.saveSong(nativeSongObj)
        if (song == null) {
            YLog.debug("PlaybackManager: Failed to save song.")
            return
        }
        val id = song.id

        if (id == currentSongId && lastSong?.lyrics.isNullOrEmpty()) {
            YLog.debug("PlaybackManager: Lyrics ready for current song $id, updating player.")
            setSong(song)
        } else {
            YLog.debug("PlaybackManager: Lyrics ready for song $id, but not current song.")
        }
    }

    /**
     * 用于重新连接后的状态同步
     */
    fun syncCurrentSong() {
        currentSongId?.let { id ->
            val song = SongRepository.getSong(id)
            setSong(song)
        }
    }

    private fun setSong(song: Song?) {
        player?.setSong(song)
        lastSong = song
    }
}