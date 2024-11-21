@file:OptIn(InternalIoApi::class, UnsafeIoApi::class)

package com.fleeksoft.io.kotlinx

import com.fleeksoft.charset.Charset
import com.fleeksoft.io.exception.EOFException
import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.Source
import kotlinx.io.UnsafeIoApi
import kotlinx.io.readByteArray
import kotlinx.io.unsafe.UnsafeBufferOperations
import com.fleeksoft.charset.decodeToString

/**
 * Decodes whole content of this stream into a string using [charset]. Returns empty string if the source is exhausted.
 *
 * @param charset the [com.fleeksoft.charset.Charset] to use for string decoding.
 *
 * @throws IllegalStateException when the source is closed.
 * @throws com.fleeksoft.io.exception.IOException when some I/O error occurs.
 *
 */

public fun Source.readString(charset: Charset): String {
    var req = 1L
    while (request(req)) {
        req *= 2
    }
    return buffer.readStringImpl(buffer.size, charset)
}

/**
 * Decodes [byteCount] bytes of this stream into a string using [charset].
 *
 * @param byteCount the number of bytes to read from the source for decoding.
 * @param charset the [Charset] to use for string decoding.
 *
 * @throws com.fleeksoft.io.exception.EOFException when the source exhausted before [byteCount] bytes could be read from it.
 * @throws IllegalStateException when the source is closed.
 * @throws IllegalArgumentException if [byteCount] is negative or its value is greater than [Int.MAX_VALUE].
 * @throws com.fleeksoft.io.exception.IOException when some I/O error occurs.
 *
 */
@OptIn(InternalIoApi::class)
public fun Source.readString(byteCount: Long, charset: Charset): String {
    require(byteCount)
    return buffer.readStringImpl(byteCount, charset)
}

internal fun Buffer.readStringImpl(byteCount: Long, charset: Charset): String {
    require(byteCount >= 0 && byteCount <= Int.MAX_VALUE) {
        "byteCount ($byteCount) is not within the range [0..${Int.MAX_VALUE})"
    }
    if (size < byteCount) {
        throw EOFException("Buffer contains less bytes then required (byteCount: $byteCount, size: $size)")
    }
    if (byteCount == 0L) return ""

    var result: String? = null
    UnsafeBufferOperations.readFromHead(this) { data, pos, limit ->
        val len = limit - pos
        if (len >= byteCount) {
            result = data.decodeToString(charset, pos, pos + byteCount.toInt())
            byteCount.toInt()
        } else {
            0
        }
    }
    return result ?: readByteArray(byteCount.toInt()).decodeToString(charset)
}

