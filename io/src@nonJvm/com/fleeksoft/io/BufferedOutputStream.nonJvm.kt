/*
 * Copyright (c) 1994, 2023, Oracle and/or its affiliates. All rights reserved.
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

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

actual open class BufferedOutputStream : FilterOutputStream {
    private var lock: SynchronizedObject = SynchronizedObject()
    protected var buf: ByteArray
    private val maxBufSize: Int
    protected var count: Int = 0

    actual constructor(out: OutputStream) : this(out, initialBufferSize(), Constants.DEFAULT_BYTE_BUFFER_SIZE)
    actual constructor(out: OutputStream, size: Int) : this(out, size, size)
    private constructor(out: OutputStream, initialSize: Int, maxSize: Int) : super(out) {
        if (initialSize <= 0) {
            throw IllegalArgumentException("Buffer size <= 0");
        }

        if (this::class == BufferedOutputStream::class) {
            this.buf = ByteArray(initialSize)    // resizable
        } else {
            this.buf = ByteArray(maxSize)
        }
        this.maxBufSize = maxSize;
    }

    private fun flushBuffer() {
        if (count > 0) {
            out.write(buf, 0, count)
            count = 0
        }
    }

    /**
     * Grow buf to fit an additional len bytes if needed.
     * If possible, it grows by len+1 to avoid flushing when len bytes
     * are added. A no-op if the buffer is not resizable.
     *
     * This method should only be called while holding the lock.
     */
    private fun growIfNeeded(len: Int) {
        var neededSize = count + len + 1
        if (neededSize < 0) neededSize = Int.MAX_VALUE
        val bufSize = buf.size
        if (neededSize > bufSize && bufSize < maxBufSize) {
            val newSize = minOf(neededSize, maxBufSize)
            buf = buf.copyOf(newSize)
        }
    }

    actual override fun write(b: Int) {
        synchronized(lock) {
            implWrite(b);
        }
    }

    private fun implWrite(b: Int) {
        growIfNeeded(1)
        if (count >= buf.size) {
            flushBuffer()
        }
        buf[count++] = b.toByte()
    }

    actual override fun write(b: ByteArray, off: Int, len: Int) {
        synchronized(lock) {
            implWrite(b, off, len)
        }
    }

    private fun implWrite(b: ByteArray, off: Int, len: Int) {
        if (len >= maxBufSize) {
            // If the request length exceeds the max size of the output buffer,
            // flush the output buffer and then write the data directly.
            // In this way, buffered streams will cascade harmlessly.
            flushBuffer()
            out.write(b, off, len)
            return
        }
        growIfNeeded(len)
        if (len > buf.size - count) {
            flushBuffer()
        }
        b.copyInto(buf, destinationOffset = count, startIndex = off, endIndex = off + len)
        count += len
    }

    actual override fun flush() {
        synchronized(lock) {
            implFlush()
        }
    }

    private fun implFlush() {
        flushBuffer()
        out.flush()
    }

    companion object {
        private fun initialBufferSize(): Int {
            return Constants.DEFAULT_BYTE_BUFFER_SIZE
            /*return if (VM.isBooted() && Thread.currentThread().isVirtual) {
                DEFAULT_INITIAL_BUFFER_SIZE
            } else {
                Constants.DEFAULT_BYTE_BUFFER_SIZE
            }*/
        }
    }

}