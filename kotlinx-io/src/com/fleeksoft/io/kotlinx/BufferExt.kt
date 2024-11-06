@file:OptIn(UnsafeIoApi::class)

package com.fleeksoft.io.kotlinx

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.decodeToString
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.EOFException
import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.readByteArray
import kotlinx.io.unsafe.UnsafeBufferOperations

/**
 * Reads and exhausts bytes from [input] into this buffer. Stops reading data on [input] exhaustion.
 *
 * @param input the stream to read data from.
 *
 */
public fun Buffer.transferFrom(input: InputStream): Buffer {
    write(input, Long.MAX_VALUE, true)
    return this
}

/**
 * Reads [byteCount] bytes from [input] into this buffer. Throws an exception when [input] is
 * exhausted before reading [byteCount] bytes.
 *
 * @param input the stream to read data from.
 * @param byteCount the number of bytes read from [input].
 *
 * @throws com.fleeksoft.io.exception.IOException when [input] exhausted before reading [byteCount] bytes from it.
 * @throws IllegalArgumentException when [byteCount] is negative.
 *
 */

internal inline fun checkByteCount(byteCount: Long) {
    require(byteCount >= 0) { "byteCount ($byteCount) < 0" }
}

public fun Buffer.write(input: InputStream, byteCount: Long): Buffer {
    checkByteCount(byteCount)
    write(input, byteCount, false)
    return this
}

@OptIn(UnsafeIoApi::class)
private fun Buffer.write(input: InputStream, byteCount: Long, forever: Boolean) {
    var remainingByteCount = byteCount
    var exchaused = false
    while (!exchaused && (remainingByteCount > 0L || forever)) {
        UnsafeBufferOperations.writeToTail(this, 1) { data, pos, limit ->
            val maxToCopy = minOf(remainingByteCount, (limit - pos).toLong()).toInt()
            val bytesRead = input.read(data, pos, maxToCopy)
            if (bytesRead == -1) {
                if (!forever) {
                    throw EOFException("Stream exhausted before $byteCount bytes were read.")
                }
                exchaused = true
                0
            } else {
                remainingByteCount -= bytesRead
                bytesRead
            }
        }
    }
}


public fun Buffer.readAtMostTo(sink: ByteBuffer): Int {
    if (exhausted()) return -1
    var toCopy = 0
    UnsafeBufferOperations.readFromHead(this) { data, pos, limit ->
        toCopy = minOf(sink.remaining(), limit - pos)
        sink.put(data, pos, toCopy)
        toCopy
    }

    return toCopy
}

public fun Buffer.transferFrom(source: ByteBuffer): Buffer {
    val byteCount = source.remaining()
    var remaining = byteCount

    while (remaining > 0) {
        UnsafeBufferOperations.writeToTail(this, 1) { data, pos, limit ->
            val toCopy = minOf(remaining, limit - pos)
            source.get(data, pos, toCopy)
            remaining -= toCopy
            toCopy
        }
    }

    return this
}


@OptIn(UnsafeIoApi::class)
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