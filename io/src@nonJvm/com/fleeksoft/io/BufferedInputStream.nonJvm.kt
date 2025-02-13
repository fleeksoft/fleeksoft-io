/*
 * Copyright (c) 1994, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.fleeksoft.io

import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.internal.ArraysSupport
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

actual open class BufferedInputStream : FilterInputStream {
    private val lock = SynchronizedObject()
    private var initialSize = 0

    private val buf: AtomicRef<ByteArray?> = atomic(null)
    private var markpos = -1
    private var marklimit = 0
    private var pos = 0
    private var count = 0

    actual constructor(input: InputStream) : this(input, Constants.DEFAULT_BYTE_BUFFER_SIZE)
    actual constructor(input: InputStream, size: Int) : super(input) {
        if (size <= 0) {
            throw IllegalArgumentException("Buffer size <= 0")
        }
        initialSize = size
        val newValue = if (this::class == BufferedInputStream::class) {
            EMPTY
        } else {
            ByteArray(size)
        }
        buf.value = newValue
    }

    /**
     * Check to make sure that underlying input stream has not been
     * nulled out due to close; if not return it;
     */
    private fun getInIfOpen(): InputStream {
        return requireNotNull(input) { "Stream closed" }
    }

    /**
     * Returns the internal buffer, optionally allocating it if empty.
     * @param allocateIfEmpty true to allocate if empty
     * @throws com.fleeksoft.io.exception.IOException if the stream is closed (buf is null)
     */
    private fun getBufIfOpen(allocateIfEmpty: Boolean): ByteArray {
        var buffer = buf.value
        if (allocateIfEmpty && buffer != null && buffer.isEmpty()) {
            buffer = ByteArray(initialSize)
            val newBuffer = ByteArray(initialSize)
            if (!buf.compareAndSet(EMPTY, newBuffer)) {
                return buf.value!!
            }
            return newBuffer
            /*if (!U.compareAndSetReference(this, BUF_OFFSET, EMPTY, buffer)) {
                // re-read buf
                buffer = buf
            }*/
        }
        if (buffer == null) throw IOException("Stream closed")
        return buffer
    }

    /**
     * Returns the internal buffer, allocating it if empty.
     * @throws com.fleeksoft.io.exception.IOException if the stream is closed (buf is null)
     */
    private fun getBufIfOpen(): ByteArray {
        return getBufIfOpen(true)
    }

    private fun ensureOpen() {
        if (buf.value == null) {
            throw IOException("Stream closed")
        }
    }

    private fun fill() {
        var buffer = getBufIfOpen()
        if (markpos == -1) {
            pos = 0 // no mark: throw away the buffer
        } else if (pos >= buffer.size) { // no room left in buffer
            if (markpos > 0) { // can throw away early part of the buffer
                val sz = pos - markpos
                buffer.copyInto(buffer, 0, markpos, pos)
                pos = sz
                markpos = 0
            } else if (buffer.size >= marklimit) {
                markpos = -1 // buffer got too big, invalidate mark
                pos = 0 // drop buffer contents
            } else { // grow buffer
                var nsz = ArraysSupport.newLength(
                    pos,
                    1, // minimum growth
                    pos // preferred growth
                )
                if (nsz > marklimit) nsz = marklimit
                val nbuf = ByteArray(nsz)
                buffer.copyInto(nbuf, 0, 0, pos)
                // Atomic compare-and-set
                if (!buf.compareAndSet(buffer, nbuf)) throw IOException("Stream closed")
                /*if (!U.compareAndSetReference(this, BUF_OFFSET, buffer, nbuf)) {
                    // Can't replace buf if there was an async close.
                    // Note: This would need to be changed if fill()
                    // is ever made accessible to multiple threads.
                    // But for now, the only way CAS can fail is via close.
                    // assert buf == null;
                    throw IOException("Stream closed")
                }*/
                buffer = nbuf
            }
        }
        count = pos
        val n = getInIfOpen().read(buffer, pos, buffer.size - pos)
        if (n > 0) count = n + pos
    }

    actual override fun read(): Int {
        return synchronized(lock) {
            if (pos >= count) {
                fill()
                if (pos >= count)
                    return -1
            }
            getBufIfOpen()[pos++].toInt() and 0xff
        }
    }

    actual override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        return synchronized(lock) {
            implRead(bytes, off, len)
        }
    }

    private fun implRead(b: ByteArray, off: Int, len: Int): Int {
        ensureOpen()
        if ((off or len or (off + len) or (b.size - (off + len))) < 0) {
            throw IndexOutOfBoundsException()
        } else if (len == 0) {
            return 0
        }

        var n = 0
        while (true) {
            val nread = read1(b, off + n, len - n)
            if (nread <= 0) {
                return if (n == 0) nread else n
            }
            n += nread
            if (n >= len) {
                return n
            }
            // if not closed but no bytes available, return
            if (input != null && input!!.available() <= 0) {
                return n
            }
        }
    }

    private fun read1(b: ByteArray, off: Int, len: Int): Int {
        var avail = count - pos
        if (avail <= 0) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, do not bother to copy the
               bytes into the local buffer.  In this way buffered streams will
               cascade harmlessly. */
            val size = maxOf(getBufIfOpen(false).size, initialSize)
            if (len >= size && markpos == -1) {
                return getInIfOpen().read(b, off, len)
            }
            fill()
            avail = count - pos
            if (avail <= 0) return -1
        }
        val cnt = if (avail < len) avail else len
        getBufIfOpen().copyInto(b, off, pos, pos + cnt)
        pos += cnt
        return cnt
    }

    actual override fun skip(n: Long): Long {
        return synchronized(lock) {
            implSkip(n)
        }
    }

    private fun implSkip(n: Long): Long {
        ensureOpen()
        if (n <= 0) {
            return 0
        }
        var avail = (count - pos).toLong()

        if (avail <= 0) {
            // If no mark position set then don't keep in buffer
            if (markpos == -1) {
                return getInIfOpen().skip(n)
            }

            // Fill in buffer to save bytes for reset
            fill()
            avail = (count - pos).toLong()
            if (avail <= 0) {
                return 0
            }
        }

        val skipped = if (avail < n) avail else n
        pos += skipped.toInt()
        return skipped
    }

    actual override fun available(): Int {
        return synchronized(lock) {
            val n = count - pos
            val avail = getInIfOpen().available()
            if (n > (Int.MAX_VALUE - avail)) Int.MAX_VALUE else n + avail
        }
    }

    actual override fun mark(readLimit: Int) {
        synchronized(lock) {
            marklimit = readLimit
            markpos = pos
        }
    }


    actual override fun reset() {
        synchronized(lock) {
            ensureOpen()
            if (markpos < 0)
                throw IOException("Resetting to invalid mark")
            pos = markpos
        }
    }

    actual override fun markSupported(): Boolean = true

    actual override fun close() {
        synchronized(lock) {
            val buffer = buf.value
            if (buffer != null) {
                buf.value = null
                input?.close()
                input = null
            }
        }
    }

    actual override fun transferTo(out: OutputStream): Long {
        requireNotNull(out) { "out" }
        return synchronized(lock) {
            implTransferTo(out)
        }
    }

    private fun implTransferTo(out: OutputStream): Long {
        if (this::class == BufferedInputStream::class && markpos == -1) {
            val avail = count - pos
            if (avail > 0) {
                if (isTrusted(out)) {
                    out.write(getBufIfOpen(), pos, avail)
                } else {
                    // Prevent poisoning and leaking of buf
                    val buffer = getBufIfOpen().copyOfRange(pos, count)
                    out.write(buffer)
                }
                pos = count
            }
            return try {
                avail.toLong().plus(getInIfOpen().transferTo(out))
            } catch (ignore: ArithmeticException) {
                Long.MAX_VALUE
            }
        } else {
            return super.transferTo(out)
        }
    }

    companion object {
        private val EMPTY = byteArrayOf()

        private fun isTrusted(os: OutputStream): Boolean {
            // FIXME: depends on FileOutputStream::class
            // FIXME: depends on PipedOutputStream::class
            val clazz = os::class
            return clazz == ByteArrayOutputStream::class /*||
                   clazz == FileOutputStream::class ||
                   clazz == PipedOutputStream::class*/
        }
    }
}