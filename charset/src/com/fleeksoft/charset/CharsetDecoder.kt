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

package com.fleeksoft.charset

import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer

expect abstract class CharsetDecoder {

    fun charset(): Charset

    fun averageCharsPerByte(): Float
    fun maxCharsPerByte(): Float

    fun malformedInputAction(): CodingErrorAction
    fun unmappableCharacterAction(): CodingErrorAction

    fun decode(byteBuffer: ByteBuffer, outCharBuffer: CharBuffer, endOfInput: Boolean): CoderResult

    fun decode(inByteBuffer: ByteBuffer): CharBuffer

    fun flush(out: CharBuffer): CoderResult


    fun onMalformedInput(newAction: CodingErrorAction): CharsetDecoder

    fun onUnmappableCharacter(newAction: CodingErrorAction): CharsetDecoder

    /**
     * Resets this decoder, clearing any internal state.
     *
     * This method resets charset-independent state and also invokes the
     * [implReset] method to perform any charset-specific reset actions.
     *
     * @return This decoder
     */
    fun reset(): CharsetDecoder
}
