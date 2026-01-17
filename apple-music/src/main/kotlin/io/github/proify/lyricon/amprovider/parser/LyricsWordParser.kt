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

import io.github.proify.lyricon.amprovider.model.LyricWord

object LyricsWordParser : Parser() {

    fun parser(any: Any): MutableList<LyricWord> {
        val words = mutableListOf<LyricWord>()
        val size = callMethod(any, "size") as? Long ?: 0
        for (i in 0..<size) {
            val ptr: Any = callMethod(any, "get", i) ?: continue
            val wordNative = callMethod(ptr, "get") ?: continue
            words.add(parserWordNative(wordNative))
        }
        return words
    }

    fun parserWordNative(o: Any): LyricWord {
        val word = LyricWord()
        LyricsTimingParser.parser(word, o)
        word.text = callMethod(o, "getHtmlLineText") as? String
        return word
    }

}