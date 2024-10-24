package com.fleeksoft.io

import com.fleeksoft.io.exception.EndOfStreamException
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.exception.OutOfMemoryError
import com.fleeksoft.io.internal.ObjHelper

actual abstract class InputStream actual constructor() {

    actual abstract fun read(): Int

    actual open fun readNBytes(len: Int): ByteArray {
        require(len >= 0) { "len < 0" }

        var bufs: MutableList<ByteArray>? = null
        var result: ByteArray? = null
        var total = 0
        var remaining = len
        var n: Int

        do {
            var buf = ByteArray(minOf(remaining, Constants.IS_DEFAULT_BYTE_BUFFER_SIZE))
            var nread = 0

            // Read to EOF which may read more or less than buffer size
            while (read(buf, nread, minOf(buf.size - nread, remaining)).also { n = it } > 0) {
                nread += n
                remaining -= n
            }

            if (nread > 0) {
                if (Constants.IS_DEFAULT_BYTE_BUFFER_SIZE - total < nread) {
                    throw OutOfMemoryError("Required array size too large")
                }
                if (nread < buf.size) {
                    buf = buf.copyOfRange(0, nread)
                }
                total += nread
                if (result == null) {
                    result = buf
                } else {
                    if (bufs == null) {
                        bufs = ArrayList()
                        bufs.add(result)
                    }
                    bufs.add(buf)
                }
            }
            // If the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0)

        if (bufs == null) {
            return if (result == null) {
                ByteArray(0)
            } else if (result.size == total) {
                result
            } else {
                result.copyOf(total)
            }
        }

        result = ByteArray(total)
        var offset = 0
        remaining = total
        for (b in bufs) {
            val count = minOf(b.size, remaining)
            b.copyInto(result, destinationOffset = offset, startIndex = 0, endIndex = count)
            offset += count
            remaining -= count
        }

        return result
    }

    actual open fun readNBytes(bytes: ByteArray, off: Int, len: Int): Int {
        ObjHelper.checkFromIndexSize(off, len, bytes.size)
        var n = 0
        while (n < len) {
            val count = read(bytes, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }

    actual open fun read(bytes: ByteArray, off: Int, len: Int): Int {
        ObjHelper.checkFromIndexSize(off, len, bytes.size)
        if (len == 0) {
            return 0
        }

        var c = read()
        if (c == -1) {
            return -1
        }
        bytes[off] = c.toByte()

        var i = 1
        try {
            for (j in 1 until len) {
                c = read()
                if (c == -1) {
                    break
                }
                bytes[off + j] = c.toByte()
                i++
            }
        } catch (ee: IOException) {
            // Handling the exception
        }
        return i
    }

    actual open fun readAllBytes(): ByteArray {
        return readNBytes(Int.MAX_VALUE)
    }

    actual open fun mark(readLimit: Int) {
    }

    actual open fun markSupported(): Boolean = false

    actual open fun reset() {
        throw IOException("mark/reset not supported")
    }

    actual open fun skip(n: Long): Long {
        var remaining = n
        var nr: Int

        if (n <= 0) {
            return 0
        }

        val size = minOf(Constants.IS_MAX_SKIP_BUFFER_SIZE, remaining)
        val skipBuffer = ByteArray(size.toInt())
        while (remaining > 0) {
            nr = read(skipBuffer, 0, minOf(size, remaining).toInt())
            if (nr < 0) {
                break
            }
            remaining -= nr.toLong()
        }

        return n - remaining
    }

    actual open fun skipNBytes(n: Long) {
        var n = n
        while (n > 0) {
            val ns = skip(n)
            if (ns in 1..n) {
                n -= ns
            } else if (ns == 0L) { // no bytes skipped
                if (read() == -1) {
                    throw EndOfStreamException()
                }
                n--
            } else { // skipped negative or too many bytes
                throw IOException("Unable to skip exactly")
            }
        }
    }

    actual open fun close() {
    }

    actual open fun available(): Int {
        return 0
    }
}