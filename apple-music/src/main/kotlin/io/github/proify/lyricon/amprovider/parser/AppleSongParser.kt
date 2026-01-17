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

package io.github.proify.lyricon.amprovider.parser

import io.github.proify.lyricon.amprovider.AppleMediaMetadata
import io.github.proify.lyricon.amprovider.model.AppleSong
import io.github.proify.lyricon.amprovider.parser.LyricsSectionParser.mergeLyrics

object AppleSongParser : Parser() {

    fun parser(songNative: Any): AppleSong = AppleSong().apply {

        adamId = callMethod(songNative, "getAdamId").toString()

        callMethod(songNative, "getAgents")?.let {
            agents = LyricsAgentParser.parserAgentVector(it)
        }

        duration = callMethod(songNative, "getDuration") as? Int ?: 0

        // language = get(o, "getLanguage") as? String
        // lyricsId = get(o, "getLyricsId") as? String
        // queueId = get(o, "getQueueId") as? Long ?: 0L

        val sections = callMethod(songNative, "getSections")
        if (sections != null) {
            lyrics = LyricsSectionParser.parserSectionVector(sections).mergeLyrics()
        }

        // timing = get(o, "getTiming") as? Long ?: 0L
        // timingName = get(o, "getAvailableTiming")?.name()
        // translation = get(o, "getTranslation") as? String
        // translationLanguages = StringVectorParser.parserStringVectorNative(get(o, "getTranslationLanguages"))

        adamId?.let {
            AppleMediaMetadata.getMetadataById(it)
                ?.let { metadata ->
                    name = metadata.title
                    artist = metadata.artist
                }
        }
    }
}