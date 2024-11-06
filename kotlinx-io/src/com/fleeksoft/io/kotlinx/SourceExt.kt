package com.fleeksoft.io.kotlinx

import com.fleeksoft.charset.Charset
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.Constants
import com.fleeksoft.io.InputStream
import kotlinx.io.InternalIoApi
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered

public fun Source.asInputStream(): InputStream = SourceInputStream(this)
public fun RawSource.asInputStream(): InputStream = SourceInputStream(this.buffered())
public fun InputStream.asSource(): RawSource = InputStreamSource(this)

/**
 * Reads at most [ByteBuffer.remaining] bytes from this source into [sink] and returns the number of bytes read.
 *
 * @param sink the sink to write the data to.
 *
 * @throws IllegalStateException when the source is closed.
 * @throws com.fleeksoft.io.exception.IOException when some I/O error occurs.
 *
 */
@OptIn(InternalIoApi::class)
public fun Source.readAtMostTo(sink: ByteBuffer): Int {
    if (buffer.size == 0L) {
        request(Constants.SEGMENT_SIZE)
        if (buffer.size == 0L) return -1
    }

    return buffer.readAtMostTo(sink)
}

/**
 * Decodes whole content of this stream into a string using [charset]. Returns empty string if the source is exhausted.
 *
 * @param charset the [com.fleeksoft.charset.Charset] to use for string decoding.
 *
 * @throws IllegalStateException when the source is closed.
 * @throws com.fleeksoft.io.exception.IOException when some I/O error occurs.
 *
 */
@OptIn(InternalIoApi::class)
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