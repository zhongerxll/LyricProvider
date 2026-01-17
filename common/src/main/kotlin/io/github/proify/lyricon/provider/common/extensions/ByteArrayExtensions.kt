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

package io.github.proify.lyricon.provider.common.extensions

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * 使用 ZLIB 算法压缩ByteArray。
 *
 * @return 压缩后的字节数组。
 */
fun ByteArray.deflate(): ByteArray {
    if (isEmpty()) return byteArrayOf()
    val deflater = Deflater()
    return try {
        deflater.setInput(this)
        deflater.finish()

        ByteArrayOutputStream().use { output ->
            val buffer = ByteArray(4096)
            while (!deflater.finished()) {
                val count = deflater.deflate(buffer)
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
    } finally {
        deflater.end()
    }
}

/**
 * 使用 ZLIB 算法解压缩字节数组。
 *
 * @return 解压后的ByteArray。
 * @throws java.util.zip.DataFormatException 如果数据格式非法。
 */
fun ByteArray.inflate(): ByteArray {
    if (isEmpty()) return byteArrayOf()
    val inflater = Inflater()
    return try {
        inflater.setInput(this)

        ByteArrayOutputStream().use { output ->
            val buffer = ByteArray(4096)
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0 && inflater.needsInput()) break
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
    } finally {
        inflater.end()
    }
}