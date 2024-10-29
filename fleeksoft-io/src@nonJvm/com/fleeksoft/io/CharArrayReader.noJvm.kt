package com.fleeksoft.io

import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.internal.ObjHelper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.math.min

actual class CharArrayReader : Reader {
    private val lock = SynchronizedObject()
    private var buf: CharArray? = null
    private var pos = 0
    private var markedPos = 0
    private var count = 0

    actual constructor(buf: CharArray) {
        this.buf = buf
        this.pos = 0
        this.count = buf.size
    }

    actual constructor(buf: CharArray, off: Int, len: Int) {
        if ((off < 0) || (off > buf.size) || (len < 0) ||
            ((off + len) < 0)
        ) {
            throw IllegalArgumentException()
        }
        this.buf = buf
        this.pos = off
        this.count = min(off + len, buf.size)
        this.markedPos = off
    }

    private fun ensureOpen() {
        if (buf == null)
            throw IOException("Stream closed")
    }

    override fun read(): Int {
        synchronized(lock) {
            ensureOpen()
            return if (pos >= count)
                -1
            else
                buf!![pos++].code
        }
    }

    actual override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val off = off
        var len = len
        synchronized(lock) {
            ensureOpen()
            ObjHelper.checkFromIndexSize(off, len, cbuf.size)
            if (len == 0) {
                return 0
            }

            if (pos >= count) {
                return -1
            }

            val avail = count - pos
            if (len > avail) {
                len = avail
            }
            if (len <= 0) {
                return 0
            }
            buf!!.copyInto(destination = cbuf, destinationOffset = off, startIndex = pos, endIndex = pos + len)
            pos += len
            return len
        }
    }

    override fun read(cb: CharBuffer): Int {
        synchronized(lock) {
            ensureOpen()

            if (pos >= count) {
                return -1
            }

            val avail = count - pos
            val len = min(avail, cb.remaining())
            cb.put(buf!!, pos, len)
            pos += len
            return len
        }
    }

    override fun skip(n: Long): Long {
        var n = n
        synchronized(lock) {
            ensureOpen()

            val avail = count - pos
            if (n > avail) {
                n = avail.toLong()
            }
            if (n < 0) {
                return 0
            }
            pos += n.toInt()
            return n
        }
    }

    override fun ready(): Boolean {
        synchronized(lock) {
            ensureOpen()
            return (count - pos) > 0
        }
    }

    override fun markSupported(): Boolean = true

    override fun mark(readAheadLimit: Int) {
        synchronized(lock) {
            ensureOpen()
            markedPos = pos
        }
    }

    override fun reset() {
        synchronized(lock) {
            ensureOpen()
            pos = markedPos
        }
    }

    actual override fun close() {
        synchronized(lock) {
            buf = null
        }
    }
}