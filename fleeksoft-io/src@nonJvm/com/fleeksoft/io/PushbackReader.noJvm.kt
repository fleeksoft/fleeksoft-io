package com.fleeksoft.io

import com.fleeksoft.io.exception.ArrayIndexOutOfBoundsException
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.internal.ObjHelper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

actual open class PushbackReader : FilterReader {
    private val lock = SynchronizedObject()
    private var buf: CharArray? = null
    private var pos: Int = 0

    actual constructor(reader: Reader, size: Int) : super(reader) {
        if (size <= 0) {
            throw IllegalArgumentException("size <= 0")
        }
        this.buf = CharArray(size)
        this.pos = size
    }

    actual constructor(reader: Reader) : this(reader, 1)

    private fun ensureOpen() {
        if (buf == null) {
            throw IOException("Stream closed")
        }
    }

    actual override fun read(): Int {
        synchronized(lock) {
            ensureOpen()
            return if (pos < buf!!.size)
                buf!![pos++].code
            else
                super.read()
        }
    }

    actual override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        var off = off
        var len = len
        synchronized(lock) {
            ensureOpen()
            try {
                ObjHelper.checkFromIndexSize(off, len, cbuf.size)
                if (len == 0) {
                    return 0
                }
                var avail = buf!!.size - pos
                if (avail > 0) {
                    if (len < avail)
                        avail = len
                    buf!!.copyInto(destination = cbuf, destinationOffset = off, startIndex = pos, endIndex = pos + avail)
                    pos += avail
                    off += avail
                    len -= avail
                }
                if (len > 0) {
                    len = super.read(cbuf, off, len)
                    if (len == -1) {
                        return if (avail == 0) -1 else avail
                    }
                    return avail + len
                }
                return avail
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IndexOutOfBoundsException()
            }
        }
    }

    actual open fun unread(c: Int) {
        synchronized(lock) {
            ensureOpen()
            if (pos == 0)
                throw IOException("Pushback buffer overflow")
            buf!![--pos] = c.toChar()
        }
    }

    actual open fun unread(cbuf: CharArray, off: Int, len: Int) {
        synchronized(lock) {
            ensureOpen()
            if (len > pos)
                throw IOException("Pushback buffer overflow")
            pos -= len
            cbuf.copyInto(
                destination = buf!!,
                destinationOffset = pos,
                startIndex = off,
                endIndex = off + len
            )
        }
    }

    actual open fun unread(cbuf: CharArray) {
        unread(cbuf, 0, cbuf.size)
    }

    actual override fun ready(): Boolean {
        synchronized(lock) {
            ensureOpen()
            return (pos < buf!!.size) || super.ready()
        }
    }

    actual override fun mark(readAheadLimit: Int) {
        throw IOException("mark/reset not supported")
    }

    actual override fun reset() {
        throw IOException("mark/reset not supported")
    }

    actual override fun markSupported(): Boolean = false

    actual override fun close() {
        synchronized(lock) {
            super.close()
            buf = null
        }
    }

    actual override fun skip(n: Long): Long {
        if (n < 0L) throw IllegalArgumentException("skip value is negative")
        var n = n
        synchronized(lock) {
            ensureOpen()
            val avail = buf!!.size - pos
            if (avail > 0) {
                if (n <= avail) {
                    pos += n.toInt()
                    return n
                } else {
                    pos = buf!!.size
                    n -= avail
                }
            }
            return avail + super.skip(n)
        }
    }
}