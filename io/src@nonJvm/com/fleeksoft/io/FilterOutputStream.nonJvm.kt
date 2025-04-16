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

import com.fleeksoft.io.internal.ObjHelper
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.concurrent.Volatile

actual open class FilterOutputStream actual constructor(protected val out: OutputStream) : OutputStream() {
    @Volatile
    private var closed: Boolean = false
    private val closeLock = SynchronizedObject()


    actual override fun write(b: Int) {
        out.write(b)
    }

    actual override fun write(b: ByteArray) {
        write(b, 0, b.size)
    }

    actual override fun write(b: ByteArray, off: Int, len: Int) {
        ObjHelper.checkFromIndexSize(off, len, b.size)
        for (i in 0 until len) {
            write(b[off + i].toInt())
        }
    }

    actual override fun flush() {
        out.flush()
    }

    actual override fun close() {
        if (closed) return
        synchronized(closeLock) {
            if (closed) return
            closed = true
        }
        var flushException: Throwable? = null
        try {
            flush()
        } catch (e: Throwable) {
            flushException = e
            throw e
        } finally {
            if (flushException == null) {
                out.close()
            } else {
                try {
                    out.close()
                } catch (closeException: Throwable) {
                    if (flushException !== closeException) {
                        closeException.addSuppressed(flushException)
                    }
                    throw closeException
                }
            }
        }
    }
}