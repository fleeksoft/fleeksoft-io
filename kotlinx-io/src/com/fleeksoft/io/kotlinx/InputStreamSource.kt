package com.fleeksoft.io.kotlinx

import com.fleeksoft.io.InputStream
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations

internal class InputStreamSource(private val input: InputStream) : RawSource {

    @OptIn(UnsafeIoApi::class)
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        if (byteCount == 0L) return 0L
        require(byteCount >= 0) { "byteCount ($byteCount) < 0" }
        try {
            var readTotal = 0L
            UnsafeBufferOperations.writeToTail(sink, 1) { data, pos, limit ->
                val maxToCopy = minOf(byteCount.toInt(), limit - pos)
                readTotal = input.read(data, pos, maxToCopy).toLong()
                if (readTotal == -1L) {
                    0
                } else {
                    readTotal.toInt()
                }
            }
            return readTotal
        } catch (e: AssertionError) {
            throw e
        }
    }

    override fun close() = input.close()

    override fun toString() = "RawSource($input)"
}