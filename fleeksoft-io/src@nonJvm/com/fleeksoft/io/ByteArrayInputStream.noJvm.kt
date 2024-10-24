package com.fleeksoft.io

import com.fleeksoft.io.exception.ArrayIndexOutOfBoundsException
import kotlin.math.min

actual open class ByteArrayInputStream : InputStream {

    protected var buf: ByteArray
    protected var pos = 0
    protected var mark = 0
    protected var count = 0

    actual constructor(buf: ByteArray) {
        this.buf = buf
        this.count = buf.size
    }

    actual constructor(buf: ByteArray, offset: Int, length: Int) {
        this.buf = buf
        this.pos = offset
        this.count = min(offset + length, buf.size)
        this.mark = offset
    }

    actual override fun read(): Int {
        return if (pos < count) buf[pos++].toInt() and 0xff else -1
    }

    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        var len = len
        // Explicitly check whether off and len is valid since customized validatePositionInfo function is not available in Kotlin
        require(off >= 0) { "offset < 0" }
        require(len >= 0) { "length < 0" }
        if (off > bytes.size || len > bytes.size - off) throw ArrayIndexOutOfBoundsException()

        if (pos >= count) return -1
        val avail = count - pos
        if (len > avail) len = avail
        if (len <= 0) return 0

        buf.copyInto(destination = bytes, destinationOffset = off, startIndex = pos, endIndex = pos + len)
        pos += len
        return len
    }

    override fun readAllBytes(): ByteArray {
        val result = buf.copyOfRange(pos, count)
        pos = count
        return result
    }

    // When the input parameter len == 0, read(byte[],int,int) method will return 0.
    // In Kotlin, we have no need to override it to return -1.
    override fun readNBytes(bytes: ByteArray, off: Int, len: Int): Int {
        val n = read(bytes, off, len)
        return if (n == -1) 0 else n
    }

    // TODO: add output stream
//    override fun transferTo(out: OutputStream): Long

    override fun skip(n: Long): Long {
        var k = (count - pos).toLong()
        if (n < k) k = if (n < 0) 0 else n

        pos += k.toInt()
        return k
    }

    override fun available(): Int {
        return count - pos
    }

    override fun markSupported(): Boolean {
        return true
    }

    override fun mark(readLimit: Int) {
        mark = pos
    }

    override fun reset() {
        pos = mark
    }

    override fun close() {}
}