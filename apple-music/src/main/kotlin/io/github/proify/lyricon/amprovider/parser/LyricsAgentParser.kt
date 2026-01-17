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

import io.github.proify.lyricon.amprovider.model.LyricAgent

object LyricsAgentParser : Parser() {

    fun parserAgentVector(any: Any): MutableList<LyricAgent> {
        val agents = mutableListOf<LyricAgent>()
        val size = callMethod(any, "size") as? Long ?: 0
        for (i in 0..<size) {
            val agentPtr: Any? = callMethod(any, "get", i)
            val agentNative: Any? = agentPtr?.let { callMethod(it, "get") }
            val agent = agentNative?.let { parserAgentNative(it) }
            agent?.let { agents.add(it) }
        }
        return agents
    }

    fun parserAgentNative(agentNative: Any): LyricAgent {
        val agent = LyricAgent()
        agent.nameTypes = callMethod(agentNative, "getNameTypes_") as? IntArray ?: intArrayOf()
        agent.type = callMethod(agentNative, "getType_") as? Long ?: 0
        agent.id = callMethod(agentNative, "getId") as? String
        agent.nameTypeNames = LyricAgent.getNameTypesNames(agent.nameTypes)
        agent.typeName = LyricAgent.getType(agent.type)?.name
        return agent
    }
}