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

@file:OptIn(InternalCharsetApi::class)
package com.fleeksoft.charset

import com.fleeksoft.charset.annotation.InternalCharsetApi
import com.fleeksoft.charset.cs.ThreadLocalCoders
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory

actual abstract class Charset protected constructor(private val csName: String, val aliases: Array<String>?) :
    Comparable<Charset> {
    actual fun name() = csName

    actual abstract fun newDecoder(): CharsetDecoder
    actual abstract fun newEncoder(): CharsetEncoder

    actual abstract fun contains(cs: Charset): Boolean

    actual fun decode(bb: ByteBuffer): CharBuffer {
        return ThreadLocalCoders.decoderFor(this)
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
            .decode(bb)
    }

    actual fun encode(cb: CharBuffer): ByteBuffer {
        return ThreadLocalCoders.encoderFor(this)
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
            .encode(cb)
    }

    actual fun encode(str: String): ByteBuffer {
        return encode(CharBufferFactory.wrap(str))
    }

    actual open fun canEncode(): Boolean = true


    actual override fun compareTo(other: Charset): Int {
        return csName.compareTo(other.csName, ignoreCase = true)
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        return other is Charset && csName == other.csName
    }

    actual override fun toString(): String {
        return csName
    }

    actual override fun hashCode(): Int {
        return csName.hashCode()
    }
}