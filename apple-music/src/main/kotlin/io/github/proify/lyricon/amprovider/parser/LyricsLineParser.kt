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

object LyricsLineParser : Parser() {

    fun parser(any: Any): MutableList<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val size = callMethod(any, "size") as? Long ?: 0
        for (i in 0..<size) {
            val ptr = callMethod(any, "get", i) ?: continue
            val lineNative = callMethod(ptr, "get") ?: continue
            lines.add(parserLyricsLineNative(lineNative))
        }
        return lines
    }

    fun parserLyricsLineNative(o: Any): LyricLine {
        val line = LyricLine()
        LyricsTimingParser.parser(line, o)

        val backgroundWords = callMethod(o, "getBackgroundWords", false)
        backgroundWords?.let { line.backgroundWords = LyricsWordParser.parser(it) }
        line.backgroundText = callMethod(o, "getHtmlBackgroundVocalsLineText") as? String

        line.text = callMethod(o, "getHtmlLineText") as? String
        val words = callMethod(o, "getWords")
        words?.let { line.words = LyricsWordParser.parser(it) }
        return line
    }
}