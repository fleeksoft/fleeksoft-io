package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

class HKSCS {
    open class Decoder protected constructor(
        cs: Charset,
        private val big5Dec: DoubleByte.Decoder,
        private val b2cBmp: Array<CharArray?>,
        private val b2cSupp: Array<CharArray?>?
    ) : DoubleByte.Decoder(cs, 0.5f, 1.0f, null, null, 0, 0, true) {
        override fun decodeSingle(b: Int): Char {
            return big5Dec.decodeSingle(b)
        }

        fun decodeBig5(b1: Int, b2: Int): Char {
            return big5Dec.decodeDouble(b1, b2)
        }

        override fun decodeDouble(b1: Int, b2: Int): Char {
            return b2cBmp[b1]!![b2 - b2Min]
        }

        open fun decodeDoubleEx(b1: Int, b2: Int): Char {
            return b2cSupp!![b1]!![b2 - b2Min]
        }

        override fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    val b1 = sa[sp].toInt() and 0xff
                    var c = decodeSingle(b1)
                    var inSize = 1
                    var outSize = 1
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                        if (sl - sp < 2) return CoderResultInternal.UNDERFLOW
                        val b2 = sa[sp + 1].toInt() and 0xff
                        inSize++
                        if (b2 < b2Min || b2 > b2Max) return CoderResultInternal.unmappableForLength(
                            2
                        )
                        c = decodeDouble(b1, b2) //bmp
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                            c = decodeDoubleEx(b1, b2) //supp
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                                c = decodeBig5(b1, b2) //big5
                                if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(2)
                            } else {
                                // supplementary character in u+2xxxx area
                                outSize = 2
                            }
                        }
                    }
                    if (dl - dp < outSize) return CoderResultInternal.OVERFLOW
                    if (outSize == 2) {
                        // supplementary characters
                        da[dp++] = Surrogate.high(0x20000 + c.code)
                        da[dp++] = Surrogate.low(0x20000 + c.code)
                    } else {
                        da[dp++] = c
                    }
                    sp += inSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        override fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()
            try {
                while (src.hasRemaining()) {
                    val b1 = src.getInt() and 0xff
                    var inSize = 1
                    var outSize = 1
                    var c = decodeSingle(b1)
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                        if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                        val b2 = src.getInt() and 0xff
                        inSize++
                        if (b2 < b2Min || b2 > b2Max) return CoderResultInternal.unmappableForLength(
                            2
                        )
                        c = decodeDouble(b1, b2) //bmp
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                            c = decodeDoubleEx(b1, b2) //supp
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                                c = decodeBig5(b1, b2) //big5
                                if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(2)
                            } else {
                                outSize = 2
                            }
                        }
                    }
                    if (dst.remaining() < outSize) return CoderResultInternal.OVERFLOW
                    if (outSize == 2) {
                        dst.put(Surrogate.high(0x20000 + c.code))
                        dst.put(Surrogate.low(0x20000 + c.code))
                    } else {
                        dst.put(c)
                    }
                    mark += inSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun decode(src: ByteArray, sp: Int, len: Int, dst: CharArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            val repl: Char = replacement()[0]
            while (sp < sl) {
                val b1 = src[sp++].toInt() and 0xff
                var c = decodeSingle(b1)
                if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                    if (sl == sp) {
                        c = repl
                    } else {
                        val b2 = src[sp++].toInt() and 0xff
                        if (b2 < b2Min || b2 > b2Max) {
                            c = repl
                        } else if ((decodeDouble(b1, b2).also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                            c = decodeDoubleEx(b1, b2) //supp
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                                c = decodeBig5(b1, b2) //big5
                                if (c == CharsetMapping.UNMAPPABLE_DECODING) c = repl
                            } else {
                                // supplementary character in u+2xxxx area
                                dst[dp++] = Surrogate.high(0x20000 + c.code)
                                dst[dp++] = Surrogate.low(0x20000 + c.code)
                                continue
                            }
                        }
                    }
                }
                dst[dp++] = c
            }
            return dp
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                decodeArrayLoop(src, dst)
            } else {
                decodeBufferLoop(src, dst)
            }
        }

        companion object {
            var b2Min: Int = 0x40
            var b2Max: Int = 0xfe

            fun initb2c(b2c: Array<CharArray?>, b2cStr: Array<String?>) {
                for (i in b2cStr.indices) {
                    if (b2cStr[i] == null) b2c[i] = DoubleByte.B2C_UNMAPPABLE
                    else b2c[i] = b2cStr[i]!!.toCharArray()
                }
            }
        }
    }

    open class Encoder protected constructor(
        cs: Charset,
        private val big5Enc: DoubleByte.Encoder,
        private val c2bBmp: Array<CharArray?>,
        private val c2bSupp: Array<CharArray?>?
    ) : DoubleByte.Encoder(cs, null, null, true) {
        fun encodeBig5(ch: Char): Int {
            return big5Enc.encodeChar(ch)
        }

        override fun encodeChar(ch: Char): Int {
            val bb = c2bBmp[ch.code shr 8]!![ch.code and 0xff].code
            if (bb == CharsetMapping.UNMAPPABLE_ENCODING) return encodeBig5(ch)
            return bb
        }

        open fun encodeSupp(cp: Int): Int {
            if ((cp and 0xf0000) != 0x20000) return CharsetMapping.UNMAPPABLE_ENCODING
            return c2bSupp!![(cp shr 8) and 0xff]!![cp and 0xff].code
        }

        override fun canEncode(c: Char): Boolean {
            return encodeChar(c) != CharsetMapping.UNMAPPABLE_ENCODING
        }

        override fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    var inSize = 1
                    var bb = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            val cp: Int
                            if ((sgp().parse(c, sa, sp, sl).also { cp = it }) < 0) return _sgp!!.error()
                            bb = encodeSupp(cp)
                            if (bb == CharsetMapping.UNMAPPABLE_ENCODING) return CoderResultInternal.unmappableForLength(2)
                            inSize = 2
                        } else {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }
                    if (bb > MAX_SINGLEBYTE) {    // DoubleByte
                        if (dl - dp < 2) return CoderResultInternal.OVERFLOW
                        da[dp++] = (bb shr 8).toByte()
                        da[dp++] = bb.toByte()
                    } else {                      // SingleByte
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = bb.toByte()
                    }
                    sp += inSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        protected fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark = src.position()
            try {
                while (src.hasRemaining()) {
                    var inSize = 1
                    val c = src.get()
                    var bb = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            val cp: Int
                            if ((sgp().parse(c, src).also { cp = it }) < 0) return _sgp!!.error()
                            bb = encodeSupp(cp)
                            if (bb == CharsetMapping.UNMAPPABLE_ENCODING) return CoderResultInternal.unmappableForLength(2)
                            inSize = 2
                        } else {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }
                    if (bb > MAX_SINGLEBYTE) {  // DoubleByte
                        if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                        dst.put((bb shr 8).toByte())
                        dst.put((bb).toByte())
                    } else {
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put(bb.toByte())
                    }
                    mark += inSize
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

        override var repl: ByteArray = replacement()
        override fun implReplaceWith(newReplacement: ByteArray) {
            repl = newReplacement
        }

        override fun encode(src: CharArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            while (sp < sl) {
                val c = src[sp++]
                var bb = encodeChar(c)
                if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                    if (!Character.isHighSurrogate(c) || sp == sl ||
                        !Character.isLowSurrogate(src[sp]) || ((encodeSupp(Character.toCodePoint(c, src[sp++])).also { bb = it })
                                == CharsetMapping.UNMAPPABLE_ENCODING)
                    ) {
                        dst[dp++] = repl[0]
                        if (repl.size > 1) dst[dp++] = repl[1]
                        continue
                    }
                }
                if (bb > MAX_SINGLEBYTE) {        // DoubleByte
                    dst[dp++] = (bb shr 8).toByte()
                    dst[dp++] = bb.toByte()
                } else {                          // SingleByte
                    dst[dp++] = bb.toByte()
                }
            }
            return dp
        }

        companion object {
            var C2B_UNMAPPABLE: CharArray = CharArray(0x100) { CharsetMapping.UNMAPPABLE_ENCODING_CHAR }

            fun initc2b(c2b: Array<CharArray?>, b2cStr: Array<String?>, pua: String?) {
                // init c2b/c2bSupp from b2cStr and supp
                val b2Min = 0x40
                c2b.fill(C2B_UNMAPPABLE)
                for (b1 in 0..0xff) {
                    val s = b2cStr[b1]
                    if (s == null) continue
                    for (i in s.indices) {
                        val c: Char = s[i]
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) continue
                        val hi = c.code shr 8
                        if (c2b[hi] == C2B_UNMAPPABLE) {
                            c2b[hi] = CharArray(0x100)
                            c2b[hi]?.fill(CharsetMapping.UNMAPPABLE_ENCODING_CHAR)
                        }
                        c2b[hi]!![c.code and 0xff] = ((b1 shl 8) or (i + b2Min)).toChar()
                    }
                }
                if (pua != null) {        // add the compatibility pua entries
                    var c = '\ue000' //first pua character
                    for (i in pua.indices) {
                        val bb: Char = pua[i]
                        if (bb != CharsetMapping.UNMAPPABLE_DECODING) {
                            val hi = c.code shr 8
                            if (c2b[hi] == C2B_UNMAPPABLE) {
                                c2b[hi] = CharArray(0x100)
                                c2b[hi]?.fill(CharsetMapping.UNMAPPABLE_ENCODING_CHAR)
                            }
                            c2b[hi]!![c.code and 0xff] = bb
                        }
                        c++
                    }
                }
            }
        }
    }
}