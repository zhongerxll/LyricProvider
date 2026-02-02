/*
 * Copyright 2026 Proify, Tomakino
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package io.github.proify.lyricon.spotifyprovider.xposed

import io.github.proify.lyricon.lyric.model.RichLyricLine
import io.github.proify.lyricon.lyric.model.Song
import io.github.proify.lyricon.spotifyprovider.xposed.api.response.LyricResponse
import io.github.proify.lyricon.spotifyprovider.xposed.api.response.LyricsData

fun LyricResponse.toSong(id: String): Song {
    val metadata = MediaMetadataCache.get(id)
    val song = Song()
    song.id = id
    song.name = metadata?.title
    song.artist = metadata?.artist
    song.duration = metadata?.duration ?: 0
    song.lyrics = lyrics.toLyrics()
    return song
}

fun LyricsData.toLyrics(): List<RichLyricLine> {
    val lyrics = mutableListOf<RichLyricLine>()
    lines.mapIndexed { index, line ->
        if (line.endTimeMs == 0L) {
            val nextLine = lines.getOrNull(index + 1)
            line.copy(endTimeMs = nextLine?.startTimeMs ?: (line.startTimeMs + 5000))
        } else line
    }.forEach { line ->
        lyrics += RichLyricLine(
            begin = line.startTimeMs,
            end = line.endTimeMs,
            duration = line.endTimeMs - line.startTimeMs,
            text = line.words,
            translation = line.transliteratedWords
        )
    }

    return lyrics
}