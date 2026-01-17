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

import io.github.proify.lyricon.amprovider.model.LyricLine
import io.github.proify.lyricon.amprovider.model.LyricSection

object LyricsSectionParser : Parser() {

    fun parserSectionVector(any: Any): MutableList<LyricSection> {
        val sections = mutableListOf<LyricSection>()
        val size = callMethod(any, "size") as Long
        for (i in 0..<size) {
            val sectionPtr = callMethod(any, "get", i) ?: continue
            val sectionNative = callMethod(sectionPtr, "get") ?: continue
            sections.add(parserSectionNative(sectionNative))
        }
        return sections
    }

    fun parserSectionNative(any: Any): LyricSection {
        val section = LyricSection()
        LyricsTimingParser.parser(section, any)

        val lines = callMethod(any, "getLines")
        lines?.let { section.lines = LyricsLineParser.parser(it) }
        return section
    }

    fun MutableList<LyricSection>.mergeLyrics(): MutableList<LyricLine> =
        this.flatMap { it.lines }.toMutableList()
}