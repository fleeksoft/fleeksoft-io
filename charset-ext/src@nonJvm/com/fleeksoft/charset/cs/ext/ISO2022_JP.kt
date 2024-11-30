package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.charset.cs.*
import com.fleeksoft.charset.cs.jis.JIS_X_0201
import com.fleeksoft.charset.cs.jis.JIS_X_0208
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

/*
 * Implementation notes:
 *
 * (1)"Standard based" (ASCII, JIS_X_0201 and JIS_X_0208) ISO2022-JP charset
 * is provided by the base implementation of this class.
 *
 * Three Microsoft ISO2022-JP variants, MS50220, MS50221 and MSISO2022JP
 * are provided via subclasses.
 *
 * (2)MS50220 and MS50221 are assumed to work the same way as Microsoft
 * CP50220 and CP50221's 7-bit implementation works by using CP5022X
 * specific JIS0208 and JIS0212 mapping tables (generated via Microsoft's
 * MultiByteToWideChar/WideCharToMultiByte APIs). The only difference
 * between these 2 classes is that MS50220 does not support singlebyte
 * halfwidth kana (Uff61-Uff9f) shiftin mechanism when "encoding", instead
 * these halfwidth kana characters are converted to their fullwidth JIS0208
 * counterparts.
 *
 * The difference between the standard JIS_X_0208 and JIS_X_0212 mappings
 * and the CP50220/50221 specific are
 *
 * 0208 mapping:
 *              1)0x213d <-> U2015 (compared to U2014)
 *              2)One way mappings for 5 characters below
 *                u2225 (ms) -> 0x2142 <-> u2016 (jis)
 *                uff0d (ms) -> 0x215d <-> u2212 (jis)
 *                uffe0 (ms) -> 0x2171 <-> u00a2 (jis)
 *                uffe1 (ms) -> 0x2172 <-> u00a3 (jis)
 *                uffe2 (ms) -> 0x224c <-> u00ac (jis)
 *                //should consider 0xff5e -> 0x2141 <-> U301c?
 *              3)NEC Row13 0x2d21-0x2d79
 *              4)85-94 ku <-> UE000,UE3AB (includes NEC selected
 *                IBM kanji in 89-92ku)
 *              5)UFF61-UFF9f -> Fullwidth 0208 KANA
 *
 * 0212 mapping:
 *              1)0x2237 <-> UFF5E (Fullwidth Tilde)
 *              2)0x2271 <-> U2116 (Numero Sign)
 *              3)85-94 ku <-> UE3AC - UE757
 *
 * (3)MSISO2022JP uses a JIS0208 mapping generated from MS932DB.b2c
 * and MS932DB.c2b by converting the SJIS codepoints back to their
 * JIS0208 counterparts. With the exception of
 *
 * (a)Codepoints with a resulting JIS0208 codepoints beyond 0x7e00 are
 *    dropped (this includs the IBM Extended Kanji/Non-kanji from 0x9321
 *    to 0x972c)
 * (b)The Unicode codepoints that the IBM Extended Kanji/Non-kanji are
 *    mapped to (in MS932) are mapped back to NEC selected IBM Kanji/
 *    Non-kanji area at 0x7921-0x7c7e.
 *
 * Compared to JIS_X_0208 mapping, this MS932 based mapping has
 *
 * (a)different mappings for 7 JIS codepoints
 *        0x213d <-> U2015
 *        0x2141 <-> UFF5E
 *        0x2142 <-> U2225
 *        0x215d <-> Uff0d
 *        0x2171 <-> Uffe0
 *        0x2172 <-> Uffe1
 *        0x224c <-> Uffe2
 * (b)added one-way c2b mappings for
 *        U00b8 -> 0x2124
 *        U00b7 -> 0x2126
 *        U00af -> 0x2131
 *        U00ab -> 0x2263
 *        U00bb -> 0x2264
 *        U3094 -> 0x2574
 *        U00b5 -> 0x264c
 * (c)NEC Row 13
 * (d)NEC selected IBM extended Kanji/Non-kanji
 *    These codepoints are mapped to the same Unicode codepoints as
 *    the MS932 does, while MS50220/50221 maps them to the Unicode
 *    private area.
 *
 * # There is also an interesting difference when compared to MS5022X
 *   0208 mapping for JIS codepoint "0x2D60", MS932 maps it to U301d
 *   but MS5022X maps it to U301e, obvious MS5022X is wrong, but...
 */
open class ISO2022_JP : Charset {

    constructor(name: String) : super(name, null)
    constructor() : super("ISO-2022-JP", null)

    open override fun contains(cs: Charset): Boolean {
        return ((cs is JIS_X_0201)
                || (cs is US_ASCII)
                || (cs is JIS_X_0208)
                || (cs is ISO2022_JP))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    protected open fun doSBKANA(): Boolean {
        return true
    }

    internal class Decoder(cs: Charset, private val dec0208: DoubleByte.Decoder, private val dec0212: DoubleByte.Decoder?) :
        CharsetDecoder(cs, 0.5f, 1.0f), DelegatableDecoder {
        private var currentState: Int
        private var previousState: Int

        constructor(cs: Charset) : this(cs, DEC0208, null)

        init {
            currentState = ASCII
            previousState = ASCII
        }

        override fun implReset() {
            currentState = ASCII
            previousState = ASCII
        }

        private fun decodeArrayLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            var inputSize: Int
            var b1: Int
            var b2: Int
            var b3: Int
            var b4: Int
            var c: Char
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    b1 = sa[sp].toInt() and 0xff
                    inputSize = 1
                    if ((b1 and 0x80) != 0) {
                        return CoderResultInternal.malformedForLength(inputSize)
                    }
                    if (b1 == ESC || b1 == SO || b1 == SI) {
                        if (b1 == ESC) {
                            if (sp + inputSize + 2 > sl) return CoderResultInternal.UNDERFLOW
                            b2 = sa[sp + inputSize++].toInt() and 0xff
                            if (b2 == '('.code) {
                                b3 = sa[sp + inputSize++].toInt() and 0xff
                                if (b3 == 'B'.code) {
                                    currentState = ASCII
                                } else if (b3 == 'J'.code) {
                                    currentState = JISX0201_1976
                                } else if (b3 == 'I'.code) {
                                    currentState = JISX0201_1976_KANA
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else if (b2 == '$'.code) {
                                b3 = sa[sp + inputSize++].toInt() and 0xff
                                if (b3 == '@'.code) {
                                    currentState = JISX0208_1978
                                } else if (b3 == 'B'.code) {
                                    currentState = JISX0208_1983
                                } else if (b3 == '('.code && dec0212 != null) {
                                    if (sp + inputSize + 1 > sl) return CoderResultInternal.UNDERFLOW
                                    b4 = sa[sp + inputSize++].toInt() and 0xff
                                    if (b4 == 'D'.code) {
                                        currentState = JISX0212_1990
                                    } else {
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                        } else if (b1 == SO) {
                            previousState = currentState
                            currentState = SHIFTOUT
                        } else if (b1 == SI) {
                            currentState = previousState
                        }
                        sp += inputSize
                        continue
                    }
                    if (dp + 1 > dl) return CoderResultInternal.OVERFLOW

                    when (currentState) {
                        ASCII -> da[dp++] = (b1 and 0xff).toChar()
                        JISX0201_1976 -> when (b1) {
                            0x5c -> da[dp++] = '\u00a5'
                            0x7e -> da[dp++] = '\u203e'
                            else -> da[dp++] = b1.toChar()
                        }

                        JISX0208_1978, JISX0208_1983 -> {
                            if (sp + inputSize + 1 > sl) return CoderResultInternal.UNDERFLOW
                            b2 = sa[sp + inputSize++].toInt() and 0xff
                            c = dec0208.decodeDouble(b1, b2)
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(inputSize)
                            da[dp++] = c
                        }

                        JISX0212_1990 -> {
                            if (sp + inputSize + 1 > sl) return CoderResultInternal.UNDERFLOW
                            b2 = sa[sp + inputSize++].toInt() and 0xff
                            c = dec0212!!.decodeDouble(b1, b2)
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(inputSize)
                            da[dp++] = c
                        }

                        JISX0201_1976_KANA, SHIFTOUT -> {
                            if (b1 > 0x5f) {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                            da[dp++] = (b1 + 0xff40).toChar()
                        }
                    }
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
            var b1: Int
            var b2: Int
            var b3: Int
            var b4: Int
            var c: Char
            var inputSize = 0
            try {
                while (src.hasRemaining()) {
                    b1 = src.getInt() and 0xff
                    inputSize = 1
                    if ((b1 and 0x80) != 0) return CoderResultInternal.malformedForLength(inputSize)
                    if (b1 == ESC || b1 == SO || b1 == SI) {
                        if (b1 == ESC) {  // ESC
                            if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                            b2 = src.getInt() and 0xff
                            inputSize++
                            if (b2 == '('.code) {
                                b3 = src.getInt() and 0xff
                                inputSize++
                                if (b3 == 'B'.code) {
                                    currentState = ASCII
                                } else if (b3 == 'J'.code) {
                                    currentState = JISX0201_1976
                                } else if (b3 == 'I'.code) {
                                    currentState = JISX0201_1976_KANA
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else if (b2 == '$'.code) {
                                b3 = src.getInt() and 0xff
                                inputSize++
                                if (b3 == '@'.code) {
                                    currentState = JISX0208_1978
                                } else if (b3 == 'B'.code) {
                                    currentState = JISX0208_1983
                                } else if (b3 == '('.code && dec0212 != null) {
                                    if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                                    b4 = src.getInt() and 0xff
                                    inputSize++
                                    if (b4 == 'D'.code) {
                                        currentState = JISX0212_1990
                                    } else {
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                        } else if (b1 == SO) {
                            previousState = currentState
                            currentState = SHIFTOUT
                        } else if (b1 == SI) { // shift back in
                            currentState = previousState
                        }
                        mark += inputSize
                        continue
                    }
                    if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW

                    when (currentState) {
                        ASCII -> dst.put((b1 and 0xff).toChar())
                        JISX0201_1976 -> when (b1) {
                            0x5c -> dst.put('\u00a5')
                            0x7e -> dst.put('\u203e')
                            else -> dst.put(b1.toChar())
                        }

                        JISX0208_1978, JISX0208_1983 -> {
                            if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                            b2 = src.getInt() and 0xff
                            inputSize++
                            c = dec0208.decodeDouble(b1, b2)
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(inputSize)
                            dst.put(c)
                        }

                        JISX0212_1990 -> {
                            if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                            b2 = src.getInt() and 0xff
                            inputSize++
                            c = dec0212!!.decodeDouble(b1, b2)
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(inputSize)
                            dst.put(c)
                        }

                        JISX0201_1976_KANA, SHIFTOUT -> {
                            if (b1 > 0x5f) {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                            dst.put((b1 + 0xff40).toChar())
                        }
                    }
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

        override fun implFlush(out: CharBuffer): CoderResult {
            return super.implFlush(out)
        }

        companion object {
            val DEC0208: DoubleByte.Decoder = JIS_X_0208().newDecoder() as DoubleByte.Decoder
        }
    }

    internal class Encoder(
        cs: Charset, private val enc0208: DoubleByte.Encoder, private val enc0212: DoubleByte.Encoder?,
        private val doSBKANA: Boolean
    ) : CharsetEncoder(cs, 4.0f, if (enc0212 != null) 9.0f else 8.0f, repl) {
        private var currentMode = ASCII
        private var replaceMode = JISX0208_1983

        constructor(cs: Charset) : this(cs, ENC0208, null, true)

        protected fun encodeSingle(inputChar: Char): Int {
            return -1
        }

        override fun implReset() {
            currentMode = ASCII
        }

        override fun implReplaceWith(newReplacement: ByteArray) {
            /* It's almost impossible to decide which charset they belong
               to. The best thing we can do here is to "guess" based on
               the length of newReplacement.
             */
            if (newReplacement.size == 1) {
                replaceMode = ASCII
            } else if (newReplacement.size == 2) {
                replaceMode = JISX0208_1983
            }
        }

        override fun implFlush(out: ByteBuffer): CoderResult {
            if (currentMode != ASCII) {
                if (out.remaining() < 3) return CoderResultInternal.OVERFLOW
                out.put(0x1b.toByte())
                out.put(0x28.toByte())
                out.put(0x42.toByte())
                currentMode = ASCII
            }
            return CoderResultInternal.UNDERFLOW
        }

        override fun canEncode(c: Char): Boolean {
            return ((c <= '\u007F') ||
                    (c.code >= 0xFF61 && c.code <= 0xFF9F) ||
                    (c == '\u00A5') ||
                    (c == '\u203E') ||
                    enc0208.canEncode(c) ||
                    (enc0212 != null && enc0212.canEncode(c)))
        }

        private val sgp: Surrogate.Parser = Surrogate.Parser()

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

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    if (c <= '\u007F') {
                        if (currentMode != ASCII) {
                            if (dl - dp < 3) return CoderResultInternal.OVERFLOW
                            da[dp++] = 0x1b.toByte()
                            da[dp++] = 0x28.toByte()
                            da[dp++] = 0x42.toByte()
                            currentMode = ASCII
                        }
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = c.code.toByte()
                    } else if (c.code >= 0xff61 && c.code <= 0xff9f && doSBKANA) {
                        //a single byte kana
                        if (currentMode != JISX0201_1976_KANA) {
                            if (dl - dp < 3) return CoderResultInternal.OVERFLOW
                            da[dp++] = 0x1b.toByte()
                            da[dp++] = 0x28.toByte()
                            da[dp++] = 0x49.toByte()
                            currentMode = JISX0201_1976_KANA
                        }
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = (c.code - 0xff40).toByte()
                    } else if (c == '\u00A5' || c == '\u203E') {
                        //backslash or tilde
                        if (currentMode != JISX0201_1976) {
                            if (dl - dp < 3) return CoderResultInternal.OVERFLOW
                            da[dp++] = 0x1b.toByte()
                            da[dp++] = 0x28.toByte()
                            da[dp++] = 0x4a.toByte()
                            currentMode = JISX0201_1976
                        }
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = if (c == '\u00A5') 0x5C.toByte() else 0x7e.toByte()
                    } else {
                        var index: Int = enc0208.encodeChar(c)
                        if (index != CharsetMapping.UNMAPPABLE_ENCODING) {
                            if (currentMode != JISX0208_1983) {
                                if (dl - dp < 3) return CoderResultInternal.OVERFLOW
                                da[dp++] = 0x1b.toByte()
                                da[dp++] = 0x24.toByte()
                                da[dp++] = 0x42.toByte()
                                currentMode = JISX0208_1983
                            }
                            if (dl - dp < 2) return CoderResultInternal.OVERFLOW
                            da[dp++] = (index shr 8).toByte()
                            da[dp++] = (index and 0xff).toByte()
                        } else if (enc0212 != null &&
                            (enc0212.encodeChar(c).also { index = it }) != CharsetMapping.UNMAPPABLE_ENCODING
                        ) {
                            if (currentMode != JISX0212_1990) {
                                if (dl - dp < 4) return CoderResultInternal.OVERFLOW
                                da[dp++] = 0x1b.toByte()
                                da[dp++] = 0x24.toByte()
                                da[dp++] = 0x28.toByte()
                                da[dp++] = 0x44.toByte()
                                currentMode = JISX0212_1990
                            }
                            if (dl - dp < 2) return CoderResultInternal.OVERFLOW
                            da[dp++] = (index shr 8).toByte()
                            da[dp++] = (index and 0xff).toByte()
                        } else {
                            if (Character.isSurrogate(c) && sgp.parse(c, sa, sp, sl) < 0) return sgp.error()
                            if ((unmappableCharacterAction() == CodingErrorActionValue.REPLACE)
                                && currentMode != replaceMode
                            ) {
                                if (dl - dp < 3) return CoderResultInternal.OVERFLOW
                                if (replaceMode == ASCII) {
                                    da[dp++] = 0x1b.toByte()
                                    da[dp++] = 0x28.toByte()
                                    da[dp++] = 0x42.toByte()
                                } else {
                                    da[dp++] = 0x1b.toByte()
                                    da[dp++] = 0x24.toByte()
                                    da[dp++] = 0x42.toByte()
                                }
                                currentMode = replaceMode
                            }
                            if (Character.isSurrogate(c)) return sgp.unmappableResult()
                            return CoderResultInternal.unmappableForLength(1)
                        }
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
            var mark = src.position()
            try {
                while (src.hasRemaining()) {
                    val c = src.get()

                    if (c <= '\u007F') {
                        if (currentMode != ASCII) {
                            if (dst.remaining() < 3) return CoderResultInternal.OVERFLOW
                            dst.put(0x1b.toByte())
                            dst.put(0x28.toByte())
                            dst.put(0x42.toByte())
                            currentMode = ASCII
                        }
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put(c.code.toByte())
                    } else if (c.code >= 0xff61 && c.code <= 0xff9f && doSBKANA) {
                        //Is it a single byte kana?
                        if (currentMode != JISX0201_1976_KANA) {
                            if (dst.remaining() < 3) return CoderResultInternal.OVERFLOW
                            dst.put(0x1b.toByte())
                            dst.put(0x28.toByte())
                            dst.put(0x49.toByte())
                            currentMode = JISX0201_1976_KANA
                        }
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put((c.code - 0xff40).toByte())
                    } else if (c == '\u00a5' || c == '\u203E') {
                        if (currentMode != JISX0201_1976) {
                            if (dst.remaining() < 3) return CoderResultInternal.OVERFLOW
                            dst.put(0x1b.toByte())
                            dst.put(0x28.toByte())
                            dst.put(0x4a.toByte())
                            currentMode = JISX0201_1976
                        }
                        if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                        dst.put(if (c == '\u00A5') 0x5C.toByte() else 0x7e.toByte())
                    } else {
                        var index: Int = enc0208.encodeChar(c)
                        if (index != CharsetMapping.UNMAPPABLE_ENCODING) {
                            if (currentMode != JISX0208_1983) {
                                if (dst.remaining() < 3) return CoderResultInternal.OVERFLOW
                                dst.put(0x1b.toByte())
                                dst.put(0x24.toByte())
                                dst.put(0x42.toByte())
                                currentMode = JISX0208_1983
                            }
                            if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                            dst.put((index shr 8).toByte())
                            dst.put((index and 0xff).toByte())
                        } else if (enc0212 != null &&
                            (enc0212.encodeChar(c).also { index = it }) != CharsetMapping.UNMAPPABLE_ENCODING
                        ) {
                            if (currentMode != JISX0212_1990) {
                                if (dst.remaining() < 4) return CoderResultInternal.OVERFLOW
                                dst.put(0x1b.toByte())
                                dst.put(0x24.toByte())
                                dst.put(0x28.toByte())
                                dst.put(0x44.toByte())
                                currentMode = JISX0212_1990
                            }
                            if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                            dst.put((index shr 8).toByte())
                            dst.put((index and 0xff).toByte())
                        } else {
                            if (Character.isSurrogate(c) && sgp.parse(c, src) < 0) return sgp.error()
                            if (unmappableCharacterAction() == CodingErrorActionValue.REPLACE
                                && currentMode != replaceMode
                            ) {
                                if (dst.remaining() < 3) return CoderResultInternal.OVERFLOW
                                if (replaceMode == ASCII) {
                                    dst.put(0x1b.toByte())
                                    dst.put(0x28.toByte())
                                    dst.put(0x42.toByte())
                                } else {
                                    dst.put(0x1b.toByte())
                                    dst.put(0x24.toByte())
                                    dst.put(0x42.toByte())
                                }
                                currentMode = replaceMode
                            }
                            if (Character.isSurrogate(c)) return sgp.unmappableResult()
                            return CoderResultInternal.unmappableForLength(1)
                        }
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
            val ENC0208: DoubleByte.Encoder = JIS_X_0208().newEncoder() as DoubleByte.Encoder

            private val repl = byteArrayOf(0x21.toByte(), 0x29.toByte())
        }
    }

    companion object {
        private const val ASCII = 0 // ESC ( B
        private const val JISX0201_1976 = 1 // ESC ( J
        private const val JISX0208_1978 = 2 // ESC $ @
        private const val JISX0208_1983 = 3 // ESC $ B
        private const val JISX0212_1990 = 4 // ESC $ ( D
        private const val JISX0201_1976_KANA = 5 // ESC ( I
        private const val SHIFTOUT = 6

        private const val ESC = 0x1b
        private const val SO = 0x0e
        private const val SI = 0x0f
    }
}
