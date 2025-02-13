package com.fleeksoft.io

import com.fleeksoft.io.exception.ArrayIndexOutOfBoundsException
import kotlin.math.min

actual open class ByteArrayInputStream : InputStream {

    private var buf: ByteArray
    private var pos = 0
    private var mark = 0
    private var count = 0

    actual constructor(buf: ByteArray) {
        this.buf = buf
        this.count = buf.size
    }

    actual constructor(buf: ByteArray, off: Int, len: Int) {
        this.buf = buf
        this.pos = off
        this.count = min(off + len, buf.size)
        this.mark = off
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

    override fun transferTo(out: OutputStream): Long {
        val len = count - pos
        if (len > 0) {
            // 'tmpbuf' is null if and only if 'out' is trusted
            val tmpbuf: ByteArray? = when (out::class) {
                // FIXME: depends on FileOutputStream::class
                // FIXME: depends on PipedOutputStream::class
                ByteArrayOutputStream::class /*, FileOutputStream::class, PipedOutputStream::class*/ -> null
                else -> ByteArray(minOf(len, Constants.MAX_TRANSFER_SIZE))
            }

            var nwritten = 0
            while (nwritten < len) {
                val nbyte = minOf(len - nwritten, Constants.MAX_TRANSFER_SIZE)
                // if 'out' is not trusted, transfer via a temporary buffer
                if (tmpbuf != null) {
                    buf.copyInto(tmpbuf, destinationOffset = 0, startIndex = pos, endIndex = pos + nbyte)
                    out.write(tmpbuf, 0, nbyte)
                } else {
                    out.write(buf, pos, nbyte)
                }
                pos += nbyte
                nwritten += nbyte
            }
            check(pos == count)
        }
        return len.toLong()
    }
}