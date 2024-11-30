/*
 * Copyright (c) 2000, 2023, Oracle and/or its affiliates. All rights reserved.
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

expect abstract class ByteBuffer : Buffer, Comparable<ByteBuffer> {
    abstract fun asReadOnlyBuffer(): ByteBuffer

    abstract fun get(): Byte
    abstract fun get(index: Int): Byte
    fun get(dst: ByteArray): ByteBuffer
    fun get(index: Int, dst: ByteArray): ByteBuffer
    open fun get(dst: ByteArray, off: Int, len: Int): ByteBuffer
    open fun get(index: Int, dst: ByteArray, off: Int, len: Int): ByteBuffer

    abstract fun put(b: Byte): ByteBuffer
    abstract fun put(index: Int, b: Byte): ByteBuffer
    fun put(src: ByteArray): ByteBuffer
    fun put(index: Int, src: ByteArray): ByteBuffer
    open fun put(src: ByteArray, off: Int, len: Int): ByteBuffer
    open fun put(index: Int, src: ByteArray, off: Int, len: Int): ByteBuffer
    fun put(src: ByteBuffer): ByteBuffer
    fun put(index: Int, src: ByteBuffer, off: Int, len: Int): ByteBuffer

    final override fun array(): ByteArray
    final override fun hasArray(): Boolean
    final override fun arrayOffset(): Int

    abstract fun compact(): ByteBuffer

    override fun compareTo(other: ByteBuffer): Int
}

fun ByteBuffer.getInt(): Int = get().toInt()
expect fun ByteBuffer.duplicateExt(): ByteBuffer