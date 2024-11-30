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

expect abstract class CharBuffer : Buffer, Comparable<CharBuffer>, Appendable, Readable {
    abstract fun asReadOnlyBuffer(): CharBuffer

    abstract fun get(): Char

//    abstract fun get(index: Int): Char

    open fun get(dstCharArray: CharArray, off: Int, len: Int): CharBuffer
    open fun get(index: Int, dstCharArray: CharArray, off: Int, len: Int): CharBuffer
    fun get(dstCharArray: CharArray): CharBuffer
    fun get(index: Int, dstCharArray: CharArray): CharBuffer
    abstract fun put(c: Char): CharBuffer
    abstract fun put(index: Int, c: Char): CharBuffer
    open fun put(src: CharArray, off: Int, len: Int): CharBuffer
    open fun put(index: Int, src: CharArray, off: Int, len: Int): CharBuffer
    fun put(src: CharArray): CharBuffer
    fun put(index: Int, src: CharArray): CharBuffer
    open fun put(src: CharBuffer): CharBuffer
    open fun put(index: Int, src: CharBuffer, off: Int, len: Int): CharBuffer
    fun put(src: String): CharBuffer
    open fun put(src: String, start: Int, end: Int): CharBuffer

    final override fun array(): CharArray
    final override fun hasArray(): Boolean
    final override fun arrayOffset(): Int

    abstract fun compact(): CharBuffer

    override fun toString(): String

    override fun compareTo(other: CharBuffer): Int
    override fun read(cb: CharBuffer): Int
    override fun append(value: CharSequence?): CharBuffer
    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): CharBuffer
    override fun append(value: Char): CharBuffer
}

expect fun CharBuffer.duplicateExt(): CharBuffer
expect fun CharBuffer.getChar(index: Int): Char