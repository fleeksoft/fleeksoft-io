package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.cs.DelegatableDecoder
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.SingleByte
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.cs.jis.JIS_X_0201
import com.fleeksoft.charset.cs.jis.JIS_X_0208
import com.fleeksoft.charset.cs.jis.JIS_X_0212
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

class IBM29626C : Charset("x-IBM29626C", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is IBM29626C))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    internal open class Decoder protected constructor(
        cs: Charset, avgCpb: Float, maxCpb: Float,
        private val dec0201: SingleByte.Decoder,
        private val dec0208: DoubleByte.Decoder,
        private val dec0212: DoubleByte.Decoder
    ) : CharsetDecoder(cs, avgCpb, maxCpb), DelegatableDecoder {

        constructor(cs: Charset) : this(cs, 0.5f, 1.0f, DEC0201, DEC0208, DEC0212)

        protected fun decodeSingle(b: Int): Char {
            if (b < 0x8e) return b.toChar()
            if (b < 0x90) return CharsetMapping.UNMAPPABLE_DECODING
            if (b < 0xa0) return b.toChar()
            return CharsetMapping.UNMAPPABLE_DECODING
        }

        protected fun decodeUDC(byte1: Int, byte2: Int, offset: Int): Char {
            if ((byte1 >= 0xf5 && byte1 <= 0xfe)
                && (byte2 >= 0xa1 && byte2 <= 0xfe)
            ) {
                return ((byte1 - 0xf5) * 94 + (byte2 - 0xa1) + offset).toChar()
            }
            return CharsetMapping.UNMAPPABLE_DECODING
        }


        protected fun decodeDouble(byte1: Int, byte2: Int): Char {
            if (byte1 == 0x8e) {
                if (byte2 < 0x80) return CharsetMapping.UNMAPPABLE_DECODING
                var c: Char = dec0201.decode(byte2)
                if (byte2 >= 0xe0 && byte2 <= 0xe4) c = g1_c[byte2 - 0xe0]
                return c
            }
            if ((byte1 >= 0xa1 && byte1 <= 0xfe)
                && (byte2 >= 0xa1 && byte2 <= 0xfe)
            ) {
                val c = ((byte1 shl 8) + byte2).toChar()
                val idx = G2_b.indexOf(c)
                if (idx > -1) return G2_c[idx]
            }
            var ch: Char = dec0208.decodeDouble(byte1 - 0x80, byte2 - 0x80)
            if (ch == CharsetMapping.UNMAPPABLE_DECODING) ch = decodeUDC(byte1, byte2, 0xe000)
            return ch
        }

        protected fun decodeDoubleG3(byte1: Int, byte2: Int): Char {
            if ((byte1 >= 0xa1 && byte1 <= 0xfe)
                && (byte2 >= 0xa1 && byte2 <= 0xfe)
            ) {
                val c = ((byte1 shl 8) + byte2).toChar()
                val idx = G3_b.indexOf(c)
                if (idx > -1) return G3_c[idx]
            }
            var ch: Char = dec0212.decodeDouble(byte1 - 0x80, byte2 - 0x80)
            if (ch == '\u2116') ch = CharsetMapping.UNMAPPABLE_DECODING
            if (ch != CharsetMapping.UNMAPPABLE_DECODING) ch = if (ibm943.canEncode(ch)) ch else CharsetMapping.UNMAPPABLE_DECODING
            if (ch == CharsetMapping.UNMAPPABLE_DECODING) ch = decodeUDC(byte1, byte2, 0xe3ac)
            return ch
        }

        private fun decodeArrayLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            var b1 = 0
            var b2 = 0
            var inputSize = 0
            var outputChar: Char = CharsetMapping.UNMAPPABLE_DECODING
            try {
                while (sp < sl) {
                    b1 = sa[sp].toInt() and 0xff
                    inputSize = 1

                    outputChar = decodeSingle(b1)
                    if (outputChar == CharsetMapping.UNMAPPABLE_DECODING) { // Multibyte char
                        if (b1 == 0x8f) {           // JIS0212
                            if (sp + 3 > sl) return CoderResultInternal.UNDERFLOW
                            b1 = sa[sp + 1].toInt() and 0xff
                            b2 = sa[sp + 2].toInt() and 0xff
                            inputSize += 2
                            outputChar = decodeDoubleG3(b1, b2)
                        } else {                     // JIS0201, JIS0208
                            if (sp + 2 > sl) return CoderResultInternal.UNDERFLOW
                            b2 = sa[sp + 1].toInt() and 0xff
                            inputSize++
                            outputChar = decodeDouble(b1, b2)
                        }
                    }
                    if (outputChar == CharsetMapping.UNMAPPABLE_DECODING) { // can't be decoded
                        return CoderResultInternal.unmappableForLength(inputSize)
                    }
                    if (dp + 1 > dl) return CoderResultInternal.OVERFLOW
                    da[dp++] = outputChar
                    sp += inputSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        private fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()
            var b1 = 0
            var b2 = 0
            var inputSize = 0
            var outputChar: Char = CharsetMapping.UNMAPPABLE_DECODING

            try {
                while (src.hasRemaining()) {
                    b1 = src.getInt() and 0xff
                    inputSize = 1
                    outputChar = decodeSingle(b1)
                    if (outputChar == CharsetMapping.UNMAPPABLE_DECODING) { // Multibyte char
                        if (b1 == 0x8f) {   // JIS0212
                            if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                            b1 = src.getInt() and 0xff
                            b2 = src.getInt() and 0xff
                            inputSize += 2
                            outputChar = decodeDoubleG3(b1, b2)
                        } else {                     // JIS0201 JIS0208
                            if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                            b2 = src.getInt() and 0xff
                            inputSize++
                            outputChar = decodeDouble(b1, b2)
                        }
                    }
                    if (outputChar == CharsetMapping.UNMAPPABLE_DECODING) {
                        return CoderResultInternal.unmappableForLength(inputSize)
                    }
                    if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                    dst.put(outputChar)
                    mark += inputSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        // Make some protected methods public for use by JISAutoDetect
        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                decodeArrayLoop(src, dst)
            } else {
                decodeBufferLoop(src, dst)
            }
        }

        override fun implReset() {
            super.implReset()
        }

        override fun implFlush(out: CharBuffer): CoderResult {
            return super.implFlush(out)
        }

        companion object {
            val DEC0201: SingleByte.Decoder = JIS_X_0201().newDecoder() as SingleByte.Decoder

            val DEC0208: DoubleByte.Decoder = JIS_X_0208().newDecoder() as DoubleByte.Decoder

            val DEC0212: DoubleByte.Decoder = JIS_X_0212().newDecoder() as DoubleByte.Decoder

            val ibm943: DoubleByte.Encoder = IBM943().newEncoder() as DoubleByte.Encoder

            private val G2_b = "\uA1F1\uA1F2\uA2CC\uADA1\uADA2\uADA3\uADA4\uADA5\uADA6\uADA7" +
                    "\uADA8\uADA9\uADAA\uADAB\uADAC\uADAD\uADAE\uADAF\uADB0\uADB1" +
                    "\uADB2\uADB3\uADB4\uADB5\uADB6\uADB7\uADB8\uADB9\uADBA\uADBB" +
                    "\uADBC\uADBD\uADBE\uADC0\uADC1\uADC2\uADC3\uADC4\uADC5\uADC6" +
                    "\uADC7\uADC8\uADC9\uADCA\uADCB\uADCC\uADCD\uADCE\uADCF\uADD0" +
                    "\uADD1\uADD2\uADD3\uADD4\uADD5\uADD6\uADDF\uADE0\uADE1\uADE2" +
                    "\uADE3\uADE4\uADE5\uADE6\uADE7\uADE8\uADE9\uADEA\uADEB\uADEC" +
                    "\uADED\uADEE\uADEF\uADF0\uADF1\uADF2\uADF3\uADF4\uADF5\uADF6" +
                    "\uADF7\uADF8\uADF9\uADFA\uADFB\uADFC"

            private val G2_c = "\uFFE0\uFFE1\uFFE2\u2460\u2461\u2462\u2463\u2464\u2465\u2466" +
                    "\u2467\u2468\u2469\u246A\u246B\u246C\u246D\u246E\u246F\u2470" +
                    "\u2471\u2472\u2473\u2160\u2161\u2162\u2163\u2164\u2165\u2166" +
                    "\u2167\u2168\u2169\u3349\u3314\u3322\u334D\u3318\u3327\u3303" +
                    "\u3336\u3351\u3357\u330D\u3326\u3323\u332B\u334A\u333B\u339C" +
                    "\u339D\u339E\u338E\u338F\u33C4\u33A1\u337B\u301D\u301F\u2116" +
                    "\u33CD\u2121\u32A4\u32A5\u32A6\u32A7\u32A8\u3231\u3232\u3239" +
                    "\u337E\u337D\u337C\u2252\u2261\u222B\u222E\u2211\u221A\u22A5" +
                    "\u2220\u221F\u22BF\u2235\u2229\u222A"

            private val G3_b = "\uF3B8\uF3B9\uF3AB\uF3AC\uF3AD\uF3AE\uF3AF\uF3B0\uF3B1\uF3B2" +
                    "\uF3B3\uF3B4\uF3A1\uF3A2\uF3A3\uF3A4\uF3A5\uF3A6\uF3A7\uF3A8" +
                    "\uF3A9\uF3AA\uF3B7\uF3B8\uF4A2\uF4A3\uF4A4\uF4A5\uF4A6\uF4A8" +
                    "\uF4A9\uF4AC\uF4AE\uF4AF\uF4B0\uF4B2\uF4B3\uF4B4\uF4B5\uF4B6" +
                    "\uF4B7\uF4BA\uF4BD\uF4BE\uF4C0\uF4BF\uF4C2\uF4A1\uF4C6\uF4C7" +
                    "\uF4C8\uF4CB\uF4D0\uF4D4\uF4D5\uF4D7\uF4D9\uF4DC\uF4DF\uF4E0" +
                    "\uF4E1\uF4E5\uF4E7\uF4EA\uF4ED\uF4EE\uF4EF\uF4F4\uF4F5\uF4F6" +
                    "\uF4F8\uF3B8\uF4B9\uF4EB\uF4A7\uF4AA\uF4AB\uF4B1\uF4B8\uF4BB" +
                    "\uF4BC\uF4C4\uF4C5\uF4C9\uF4CC\uF4CD\uF4CE\uF4CF\uF4D1\uF4D3" +
                    "\uF4D6\uF4D8\uF4DA\uF4DB\uF4DE\uF4E2\uF4E3\uF4E4\uF4E6\uF4E8" +
                    "\uF4E9\uF4EC\uF4F1\uF4F2\uF4F3\uF4F7\uF3B6\uF3B5"

            private val G3_c = "\u2116\u2121\u2160\u2161\u2162\u2163\u2164\u2165\u2166\u2167" +
                    "\u2168\u2169\u2170\u2171\u2172\u2173\u2174\u2175\u2176\u2177" +
                    "\u2178\u2179\u3231\u00A6\u4EFC\u50F4\u51EC\u5307\u5324\u548A" +
                    "\u5759\u589E\u5BEC\u5CF5\u5D53\u5FB7\u6085\u6120\u654E\u663B" +
                    "\u6665\u6801\u6A6B\u6AE2\u6DF2\u6DF8\u7028\u70BB\u7501\u7682" +
                    "\u769E\u7930\u7AE7\u7DA0\u7DD6\u8362\u85B0\u8807\u8B7F\u8CF4" +
                    "\u8D76\u90DE\u9115\u9592\u973B\u974D\u9751\u999E\u9AD9\u9B72" +
                    "\u9ED1\uF86F\uF929\uF9DC\uFA0E\uFA0F\uFA10\uFA11\uFA12\uFA13" +
                    "\uFA14\uFA15\uFA16\uFA17\uFA18\uFA19\uFA1A\uFA1B\uFA1C\uFA1D" +
                    "\uFA1E\uFA1F\uFA20\uFA21\uFA22\uFA23\uFA24\uFA25\uFA26\uFA27" +
                    "\uFA28\uFA29\uFA2A\uFA2B\uFA2C\uFA2D\uFF02\uFF07"

            val g1_c: String = "\u00a2\u00a3\u00ac\\\u007e"
        }
    }


    internal open class Encoder protected constructor(
        cs: Charset, avgBpc: Float, maxBpc: Float,
        private val enc0201: SingleByte.Encoder,
        private val enc0208: DoubleByte.Encoder,
        private val enc0212: DoubleByte.Encoder
    ) : CharsetEncoder(cs, avgBpc, maxBpc) {
        private val sgp: Surrogate.Parser = Surrogate.Parser()

        constructor(cs: Charset) : this(cs, 3.0f, 3.0f, ENC0201, ENC0208, ENC0212)

        override fun canEncode(c: Char): Boolean {
            val encodedBytes = ByteArray(3)
            return encodeSingle(c, encodedBytes) != 0 ||
                    encodeDouble(c) != CharsetMapping.UNMAPPABLE_ENCODING
        }

        protected fun encodeSingle(inputChar: Char, outputByte: ByteArray): Int {
            if (inputChar.code >= 0x80 && inputChar.code < 0x8e) {
                outputByte[0] = inputChar.code.toByte()
                return 1
            }
            if (inputChar.code >= 0x90 && inputChar.code < 0xa0) {
                outputByte[0] = inputChar.code.toByte()
                return 1
            }
            var b: Int = enc0201.encode(inputChar)
            if (b == CharsetMapping.UNMAPPABLE_ENCODING) {
                val idx = G1_c.indexOf(inputChar)
                if (idx > -1) b = 0xe0 + idx
            }
            if (b == CharsetMapping.UNMAPPABLE_ENCODING) return 0
            if (b >= 0 && b < 128) {
                outputByte[0] = b.toByte()
                return 1
            }
            outputByte[0] = 0x8e.toByte()
            outputByte[1] = b.toByte()
            return 2
        }

        protected fun encodeUDC(ch: Char): Int {
            if (ch >= '\ue000' && ch <= '\ue757') {
                if (ch < '\ue3ac') {
                    val offset = ch.code - 0xe000
                    val b = ((offset / 94) shl 8) + (offset % 94)
                    return b + 0xf5a1
                } else {
                    val offset = ch.code - 0xe3ac
                    val b = ((offset / 94) shl 8) + (offset % 94)
                    return b + 0x8ff5a1
                }
            }
            return CharsetMapping.UNMAPPABLE_ENCODING
        }

        protected fun encodeDouble(ch: Char): Int {
            var idx = G2_c.indexOf(ch)
            if (idx > -1) return G2_b[idx].code
            idx = G3_c.indexOf(ch)
            if (idx > -1) return G3_b[idx].code + 0x8f0000
            var b: Int = enc0208.encodeChar(ch)
            if (b != CharsetMapping.UNMAPPABLE_ENCODING) return b + 0x8080
            b = encodeUDC(ch)
            if (b != CharsetMapping.UNMAPPABLE_ENCODING) return b
            if (ibm943.canEncode(ch)) {
                b = enc0212.encodeChar(ch)
                if (b != CharsetMapping.UNMAPPABLE_ENCODING) {
                    b += 0x8F8080
                    return b
                }
            }
            return b
        }

        private fun encodeArrayLoop(
            src: CharBuffer,
            dst: ByteBuffer
        ): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            var outputSize = 0
            var outputByte: ByteArray?
            val tmpBuf = ByteArray(3)

            try {
                while (sp < sl) {
                    outputByte = tmpBuf
                    val c = sa[sp]
                    if (Character.isSurrogate(c)) {
                        if (sgp.parse(c, sa, sp, sl) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }
                    outputSize = encodeSingle(c, outputByte)
                    if (outputSize == 0) { // DoubleByte
                        val ncode = encodeDouble(c)
                        if (ncode != CharsetMapping.UNMAPPABLE_ENCODING) {
                            if ((ncode and 0xFF0000) == 0) {
                                outputByte[0] = ((ncode and 0xff00) shr 8).toByte()
                                outputByte[1] = (ncode and 0xff).toByte()
                                outputSize = 2
                            } else {
                                outputByte[0] = 0x8f.toByte()
                                outputByte[1] = ((ncode and 0xff00) shr 8).toByte()
                                outputByte[2] = (ncode and 0xff).toByte()
                                outputSize = 3
                            }
                        } else {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }
                    if (dl - dp < outputSize) return CoderResultInternal.OVERFLOW
                    // Put the byte in the output buffer
                    for (i in 0..<outputSize) {
                        da[dp++] = outputByte[i]
                    }
                    sp++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        private fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var outputSize = 0
            var outputByte: ByteArray?
            val tmpBuf = ByteArray(3)

            var mark = src.position()

            try {
                while (src.hasRemaining()) {
                    outputByte = tmpBuf
                    val c = src.get()
                    if (Character.isSurrogate(c)) {
                        if (sgp.parse(c, src) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }
                    outputSize = encodeSingle(c, outputByte)
                    if (outputSize == 0) { // DoubleByte
                        val ncode = encodeDouble(c)
                        if (ncode != CharsetMapping.UNMAPPABLE_ENCODING) {
                            if ((ncode and 0xFF0000) == 0) {
                                outputByte[0] = ((ncode and 0xff00) shr 8).toByte()
                                outputByte[1] = (ncode and 0xff).toByte()
                                outputSize = 2
                            } else {
                                outputByte[0] = 0x8f.toByte()
                                outputByte[1] = ((ncode and 0xff00) shr 8).toByte()
                                outputByte[2] = (ncode and 0xff).toByte()
                                outputSize = 3
                            }
                        } else {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }
                    if (dst.remaining() < outputSize) return CoderResultInternal.OVERFLOW
                    // Put the byte in the output buffer
                    for (i in 0..<outputSize) {
                        dst.put(outputByte[i])
                    }
                    mark++
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
            val ENC0201: SingleByte.Encoder = JIS_X_0201().newEncoder() as SingleByte.Encoder

            val ENC0208: DoubleByte.Encoder = JIS_X_0208().newEncoder() as DoubleByte.Encoder

            val ENC0212: DoubleByte.Encoder = JIS_X_0212().newEncoder() as DoubleByte.Encoder

            val ibm943: DoubleByte.Encoder = IBM943().newEncoder() as DoubleByte.Encoder

            private val G2_c = "\u2015\u2211\u221F\u2225\u222E\u22BF\u2460\u2461\u2462\u2463" +
                    "\u2464\u2465\u2466\u2467\u2468\u2469\u246A\u246B\u246C\u246D" +
                    "\u246E\u246F\u2470\u2471\u2472\u2473\u301D\u301F\u3232\u3239" +
                    "\u32A4\u32A5\u32A6\u32A7\u32A8\u3303\u330D\u3314\u3318\u3322" +
                    "\u3323\u3326\u3327\u332B\u3336\u333B\u3349\u334A\u334D\u3351" +
                    "\u3357\u337B\u337C\u337D\u337E\u338E\u338F\u339C\u339D\u339E" +
                    "\u33A1\u33C4\u33CD\u4FE0\u525D\u555E\u5699\u56CA\u5861\u5C5B" +
                    "\u5C62\u6414\u6451\u6522\u6805\u688E\u6F51\u7006\u7130\u7626" +
                    "\u79B1\u7C1E\u7E48\u7E61\u7E6B\u8141\u8346\u840A\u8523\u87EC" +
                    "\u881F\u8EC0\u91AC\u91B1\u9830\u9839\u985A\u9A52\u9DD7\u9E7C" +
                    "\u9EB4\u9EB5\uFF0D\uFF5E\uFFE0\uFFE1\uFFE2"

            private val G2_b = "\uA1BD\uADF4\uADF8\uA1C2\uADF3\uADF9\uADA1\uADA2\uADA3\uADA4" +
                    "\uADA5\uADA6\uADA7\uADA8\uADA9\uADAA\uADAB\uADAC\uADAD\uADAE" +
                    "\uADAF\uADB0\uADB1\uADB2\uADB3\uADB4\uADE0\uADE1\uADEB\uADEC" +
                    "\uADE5\uADE6\uADE7\uADE8\uADE9\uADC6\uADCA\uADC1\uADC4\uADC2" +
                    "\uADCC\uADCB\uADC5\uADCD\uADC7\uADCF\uADC0\uADCE\uADC3\uADC8" +
                    "\uADC9\uADDF\uADEF\uADEE\uADED\uADD3\uADD4\uADD0\uADD1\uADD2" +
                    "\uADD6\uADD5\uADE3\uB6A2\uC7ED\uB0A2\uB3FA\uC7B9\uC5B6\uD6A2" +
                    "\uBCC8\uC1DF\uC4CF\uDAB9\uBAF4\uDBF4\uC8AE\uC6C2\uB1EB\uC1E9" +
                    "\uC5F8\uC3BD\uE5DA\uBDAB\uB7D2\uE7A6\uB7D5\uCDE9\uBED5\uC0E6" +
                    "\uCFB9\uB6ED\uBEDF\uC8B0\uCBCB\uF0F8\uC5BF\uC2CD\uB2AA\uB8B4" +
                    "\uB9ED\uCCCD\uA1DD\uA1C1\uA1F1\uA1F2\uA2CC"

            private val G3_c = "\u2116\u2121\u2160\u2161\u2162\u2163\u2164\u2165\u2166\u2167" +
                    "\u2168\u2169\u2170\u2171\u2172\u2173\u2174\u2175\u2176\u2177" +
                    "\u2178\u2179\u3231\u4EFC\u50F4\u51EC\u5307\u5324\u548A\u5759" +
                    "\u589E\u5BEC\u5CF5\u5D53\u5FB7\u6085\u6120\u654E\u663B\u6665" +
                    "\u6801\u6A6B\u6AE2\u6DF2\u6DF8\u7028\u70BB\u7501\u7682\u769E" +
                    "\u7930\u7AE7\u7DA0\u7DD6\u8362\u85B0\u8807\u8B7F\u8CF4\u8D76" +
                    "\u90DE\u9115\u9592\u973B\u974D\u9751\u999E\u9AD9\u9B72\u9ED1" +
                    "\uF86F\uF929\uF9DC\uFA0E\uFA0F\uFA10\uFA11\uFA12\uFA13\uFA14" +
                    "\uFA15\uFA16\uFA17\uFA18\uFA19\uFA1A\uFA1B\uFA1C\uFA1D\uFA1E" +
                    "\uFA1F\uFA20\uFA21\uFA22\uFA23\uFA24\uFA25\uFA26\uFA27\uFA28" +
                    "\uFA29\uFA2A\uFA2B\uFA2C\uFA2D\uFF02\uFF07\uFFE4"

            private val G3_b = "\uF3B8\uF3B9\uF3AB\uF3AC\uF3AD\uF3AE\uF3AF\uF3B0\uF3B1\uF3B2" +
                    "\uF3B3\uF3B4\uF3A1\uF3A2\uF3A3\uF3A4\uF3A5\uF3A6\uF3A7\uF3A8" +
                    "\uF3A9\uF3AA\uF3B7\uF4A2\uF4A3\uF4A4\uF4A5\uF4A6\uF4A8\uF4A9" +
                    "\uF4AC\uF4AE\uF4AF\uF4B0\uF4B2\uF4B3\uF4B4\uF4B5\uF4B6\uF4B7" +
                    "\uF4BA\uF4BD\uF4BE\uF4C0\uF4BF\uF4C2\uF4A1\uF4C6\uF4C7\uF4C8" +
                    "\uF4CB\uF4D0\uF4D4\uF4D5\uF4D7\uF4D9\uF4DC\uF4DF\uF4E0\uF4E1" +
                    "\uF4E5\uF4E7\uF4EA\uF4ED\uF4EE\uF4EF\uF4F4\uF4F5\uF4F6\uF4F8" +
                    "\uF3B8\uF4B9\uF4EB\uF4A7\uF4AA\uF4AB\uF4B1\uF4B8\uF4BB\uF4BC" +
                    "\uF4C4\uF4C5\uF4C9\uF4CC\uF4CD\uF4CE\uF4CF\uF4D1\uF4D3\uF4D6" +
                    "\uF4D8\uF4DA\uF4DB\uF4DE\uF4E2\uF4E3\uF4E4\uF4E6\uF4E8\uF4E9" +
                    "\uF4EC\uF4F1\uF4F2\uF4F3\uF4F7\uF3B6\uF3B5\uA2C3"

            private val G1_c = "\u00A2\u00A3\u00AC"
        }
    }
}
