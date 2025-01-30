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

package com.fleeksoft.charset.cs.iso

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.internal.assert
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.cs.US_ASCII
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import kotlin.math.min

class ISO_8859_1 private constructor() : Charset("ISO-8859-1", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs is US_ASCII) || (cs is ISO_8859_1))
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

            val decodeLen: Int = min(sl - sp, dl - dp)
            JLA.inflateBytesToChars(sa, sp, da, dp, decodeLen)
            sp += decodeLen
            dp += decodeLen
            src.position(sp - soff)
            dst.position(dp - doff)
            if (sl - sp > dl - dp) {
                return CoderResultInternal.OVERFLOW
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
                    if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                    dst.put((b.toInt() and 0xff).toChar())
                    mark++
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
            return c <= '\u00FF'
        }

        override fun isLegalReplacement(repl: ByteArray): Boolean {
            return true // we accept any byte value
        }

        private val sgp = Surrogate.Parser()

        fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa: CharArray = src.array()
            val soff: Int = src.arrayOffset()
            var sp: Int = soff + src.position()
            val sl: Int = soff + src.limit()
            assert(sp <= sl)
            sp = (if (sp <= sl) sp else sl)
            val da: ByteArray = dst.array()
            val doff: Int = dst.arrayOffset()
            var dp: Int = doff + dst.position()
            val dl: Int = doff + dst.limit()
            assert(dp <= dl)
            dp = (if (dp <= dl) dp else dl)
            val dlen = dl - dp
            val slen = sl - sp
            val len = if (dlen < slen) dlen else slen
            try {
                val ret = encodeISOArray(sa, sp, da, dp, len)
                sp = sp + ret
                dp = dp + ret
                if (ret != len) {
                    if (sgp.parse(sa[sp], sa, sp, sl) < 0) return sgp.error()
                    return sgp.unmappableResult()
                }
                if (len < slen) return CoderResultInternal.OVERFLOW
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - soff)
                dst.position(dp - doff)
            }
        }

        fun encodeBufferLoop(
            src: CharBuffer,
            dst: ByteBuffer
        ): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    if (c <= '\u00FF') {
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

        companion object {
            // Method possible replaced with a compiler intrinsic.
            private fun encodeISOArray(
                sa: CharArray, sp: Int,
                da: ByteArray, dp: Int, len: Int
            ): Int {
                if (len <= 0) {
                    return 0
                }
                return implEncodeISOArray(sa, sp, da, dp, len)
            }

            // TODO: @IntrinsicCandidate
            private fun implEncodeISOArray(
                sa: CharArray, sp: Int,
                da: ByteArray, dp: Int, len: Int
            ): Int {
                var sp = sp
                var dp = dp
                var i = 0
                while (i < len) {
                    val c = sa[sp++]
                    if (c > '\u00FF') break
                    da[dp++] = c.code.toByte()
                    i++
                }
                return i
            }
        }
    }

    companion object {
        val INSTANCE: ISO_8859_1 = ISO_8859_1()
    }
}