package com.fleeksoft.io.kotlinx

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