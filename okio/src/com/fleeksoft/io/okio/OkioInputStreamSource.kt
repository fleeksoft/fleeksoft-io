package com.fleeksoft.io.okio

import com.fleeksoft.io.InputStream
import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.math.min

internal open class OkioInputStreamSource(
    private val input: InputStream
) : Source {
    private val timeout: Timeout = Timeout.NONE
    override fun read(sink: Buffer, byteCount: Long): Long {
        if (byteCount == 0L) return 0L
        require(byteCount >= 0L) { "byteCount < 0: $byteCount" }
        if (input.available() < 1) return -1
        val size = min(input.available(), byteCount.toInt())
        val byteArray = ByteArray(size)
        input.read(byteArray)
        sink.write(byteArray)
        return size.toLong()
    }

    override fun timeout(): Timeout = timeout

    override fun close() = input.close()

    override fun toString() = "source($input)"
}