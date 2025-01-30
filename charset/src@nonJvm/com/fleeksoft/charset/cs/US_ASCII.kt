/*
 * Copyright (c) 2000, 2021, Oracle and/or its affiliates. All rights reserved.
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

package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.internal.assert
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import kotlin.math.min


class US_ASCII private constructor() : Charset("US-ASCII", null) {

    override fun contains(cs: Charset): Boolean {
        return (cs is US_ASCII)
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f) {
        fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa: ByteArray = src.array()
            val soff: Int = src.arrayOffset()
            var sp: Int = soff + src.position()
            val sl: Int = soff + src.limit()

            val da: CharArray = dst.array()
            val doff: Int = dst.arrayOffset()
            var dp: Int = doff + dst.position()
            val dl: Int = doff + dst.limit()

            // ASCII only loop
            val n: Int = JLA.decodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
            sp += n
            dp += n
            src.position(sp - soff)
            dst.position(dp - doff)
            if (sp < sl) {
                if (dp >= dl) {
                    return CoderResultInternal.OVERFLOW
                }
                return CoderResultInternal.malformedForLength(1)
            }
            return CoderResultInternal.UNDERFLOW
        }

        fun decodeBufferLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val b: Byte = src.get()
                    if (b >= 0) {
                        if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                        dst.put(Char(b.toUShort()))
                        mark++
                        continue
                    }
                    return CoderResultInternal.malformedForLength(1)
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                decodeArrayLoop(src, dst)
            } else {
                decodeBufferLoop(src, dst)
            }
        }
    }

    private class Encoder(cs: Charset) : CharsetEncoder(cs, 1.0f, 1.0f) {
        override fun canEncode(c: Char): Boolean {
            return c.code < 0x80
        }

        override fun isLegalReplacement(repl: ByteArray): Boolean {
            return (repl.size == 1 && repl[0] >= 0) ||
                    super.isLegalReplacement(repl)
        }

        private val sgp = Surrogate.Parser()
        fun encodeArrayLoop(
            src: CharBuffer,
            dst: ByteBuffer
        ): CoderResult {
            val sa: CharArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            val sl: Int = src.arrayOffset() + src.limit()
            assert(sp <= sl)
            sp = (if (sp <= sl) sp else sl)
            val da: ByteArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()
            assert(dp <= dl)
            dp = (if (dp <= dl) dp else dl)

            val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
            sp += n
            dp += n

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    if (c.code < 0x80) {
                        if (dp >= dl) return CoderResultInternal.OVERFLOW
                        da[dp] = c.code.toByte()
                        sp++
                        dp++
                        continue
                    }
                    if (sgp.parse(c, sa, sp, sl) < 0) return sgp.error()
                    return sgp.unmappableResult()
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    if (c.code < 0x80) {
                        if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                        dst.put(c.code.toByte())
                        mark++
                        continue
                    }
                    if (sgp.parse(c, src) < 0) return sgp.error()
                    return sgp.unmappableResult()
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                encodeArrayLoop(src, dst)
            } else {
                encodeBufferLoop(src, dst)
            }
        }
    }

    companion object {
        val INSTANCE: US_ASCII = US_ASCII()
    }
}