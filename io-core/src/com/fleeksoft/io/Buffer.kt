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

expect abstract class Buffer {
    fun remaining(): Int
    fun capacity(): Int
    fun limit(): Int

    abstract fun isReadOnly(): Boolean
    fun position(): Int
    fun hasRemaining(): Boolean
    abstract fun arrayOffset(): Int

    abstract fun array(): Any
    abstract fun hasArray(): Boolean

    // FIXME: issue on android in ByteBuffer and CharBuffer these functions return Buffer but in JVM it return self class
//    open fun position(pos: Int): Buffer
//    open fun limit(newLimit: Int): Buffer
//    open fun clear(): Buffer
//    open fun flip(): Buffer
//    open fun rewind(): Buffer
//    open fun mark(): Buffer
//    open fun reset(): Buffer
//    abstract fun duplicate(): Buffer
//    abstract fun slice(): Buffer
//    abstract fun slice(index: Int, length: Int): Buffer
}

expect fun Buffer.setPositionExt(pos: Int): Buffer
expect fun Buffer.setLimitExt(newLimit: Int): Buffer
expect fun Buffer.clearExt(): Buffer
expect fun Buffer.flipExt(): Buffer
expect fun Buffer.rewindExt(): Buffer
expect fun Buffer.markExt(): Buffer
expect fun Buffer.resetExt(): Buffer
expect fun Buffer.sliceExt(): Buffer
expect fun Buffer.sliceExt(index: Int, length: Int): Buffer