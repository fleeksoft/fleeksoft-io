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

import com.fleeksoft.io.internal.ArraysSupport
import com.fleeksoft.io.internal.ObjHelper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

actual open class ByteArrayOutputStream : OutputStream {
    protected var buf: ByteArray
    protected var count: Int = 0
    private val lock = SynchronizedObject()

    actual constructor() : this(32)
    actual constructor(size: Int) {
        if (size < 0) {
            throw IllegalArgumentException("Negative initial size: $size")
        }
        buf = ByteArray(size)
    }

    private fun ensureCapacity(minCapacity: Int) {
        // Overflow-conscious code
        val oldCapacity = buf.size
        val minGrowth = minCapacity - oldCapacity
        if (minGrowth > 0) {
            buf = buf.copyOf(ArraysSupport.newLength(oldCapacity, minGrowth, oldCapacity))
        }
    }

    actual override fun write(b: Int) {
        synchronized(lock) {
            ensureCapacity(count + 1)
            buf[count] = b.toByte()
            count += 1
        }
    }

    actual override fun write(b: ByteArray, off: Int, len: Int) {
        synchronized(lock) {
            ObjHelper.checkFromIndexSize(off, len, b.size)
            ensureCapacity(count + len)
            b.copyInto(buf, destinationOffset = count, startIndex = off, endIndex = off + len)
            count += len
        }
    }

    actual open fun writeBytes(b: ByteArray) {
        write(b, 0, b.size)
    }

    actual open fun writeTo(out: OutputStream) {
        // FIXME: check for virtual thread
        synchronized(lock) {
            out.write(buf, 0, count)
        }
    }

    actual open fun reset() {
        synchronized(lock) {
            count = 0
        }
    }

    actual open fun toByteArray(): ByteArray {
        synchronized(lock) {
            return buf.copyOf(count)
        }
    }

    actual open fun size(): Int = synchronized(lock) { count }

    actual override fun toString(): String = synchronized(lock) { buf.decodeToString(0, count) }
    actual override fun close() {

    }

}