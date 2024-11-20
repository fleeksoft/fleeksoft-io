package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.min

object DoubleByte {
    val B2C_UNMAPPABLE: CharArray = CharArray(0x100) { CharsetMapping.UNMAPPABLE_DECODING }

    open class Decoder(
        cs: Charset,
        avgcpb: Float,
        maxcpb: Float,
        val b2c: Array<CharArray?>?,
        val b2cSB: CharArray?,
        val b2Min: Int,
        val b2Max: Int,
        val _isASCIICompatible: Boolean = false
    ) : CharsetDecoder(cs, avgcpb, maxcpb), DelegatableDecoder, ArrayDecoder {

        // for SimpleEUC override
        protected open fun crMalformedOrUnderFlow(b: Int): CoderResult {
            return CoderResultInternal.UNDERFLOW
        }

        protected open fun crMalformedOrUnmappable(b1: Int, b2: Int): CoderResult {
            if (b2c!![b1] == B2C_UNMAPPABLE ||  // isNotLeadingByte(b1)
                b2c[b2] != B2C_UNMAPPABLE ||  // isLeadingByte(b2)
                decodeSingle(b2) != CharsetMapping.UNMAPPABLE_DECODING
            ) {  // isSingle(b2)
                return CoderResultInternal.malformedForLength(1)
            }
            return CoderResultInternal.unmappableForLength(2)
        }

        constructor(
            cs: Charset, b2c: Array<CharArray?>, b2cSB: CharArray, b2Min: Int, b2Max: Int,
            isASCIICompatible: Boolean
        ) : this(cs, 0.5f, 1.0f, b2c, b2cSB, b2Min, b2Max, isASCIICompatible)

        constructor(cs: Charset, b2c: Array<CharArray?>, b2cSB: CharArray, b2Min: Int, b2Max: Int) :
                this(cs, 0.5f, 1.0f, b2c, b2cSB, b2Min, b2Max, false)

        protected open fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa: ByteArray = src.array()
            val soff: Int = src.arrayOffset()
            var sp: Int = soff + src.position()
            val sl: Int = soff + src.limit()

            val da: CharArray = dst.array()
            val doff: Int = dst.arrayOffset()
            var dp: Int = doff + dst.position()
            val dl: Int = doff + dst.limit()

            try {
                if (_isASCIICompatible) {
                    val n: Int = JLA.decodeASCII(sa, sp, da, dp, min(dl - dp, sl - sp))
                    dp += n
                    sp += n
                }
                while (sp < sl && dp < dl) {
                    // inline the decodeSingle/Double() for better performance
                    var inSize = 1
                    val b1 = sa[sp].toInt() and 0xff
                    var c = b2cSB!![b1]
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                        if (sl - sp < 2) return crMalformedOrUnderFlow(b1)
                        val b2 = sa[sp + 1].toInt() and 0xff
                        if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                            return crMalformedOrUnmappable(b1, b2)
                        }
                        inSize++
                    }
                    da[dp++] = c
                    sp += inSize
                }
                return if (sp >= sl)
                    CoderResultInternal.UNDERFLOW
                else
                    CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - soff)
                dst.position(dp - doff)
            }
        }

        protected open fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining() && dst.hasRemaining()) {
                    val b1: Int = src.getInt() and 0xff
                    var c = b2cSB!![b1]
                    var inSize = 1
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                        if (src.remaining() < 1) return crMalformedOrUnderFlow(b1)
                        val b2: Int = src.getInt() and 0xff
                        if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also {
                                c = it
                            }) == CharsetMapping.UNMAPPABLE_DECODING) return crMalformedOrUnmappable(
                            b1,
                            b2
                        )
                        inSize++
                    }
                    dst.put(c)
                    mark += inSize
                }
                return if (src.hasRemaining())
                    CoderResultInternal.UNDERFLOW
                else
                    CoderResultInternal.UNDERFLOW
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

        override fun decode(src: ByteArray, sp: Int, len: Int, dst: CharArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            val repl: Char = replacement()[0]
            while (sp < sl) {
                val b1 = src[sp++].toInt() and 0xff
                var c = b2cSB!![b1]
                if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                    if (sp < sl) {
                        val b2 = src[sp++].toInt() and 0xff
                        if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                            if (crMalformedOrUnmappable(b1, b2).length() == 1) {
                                sp--
                            }
                        }
                    }
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                        c = repl
                    }
                }
                dst[dp++] = c
            }
            return dp
        }

        override fun implReset() {
            super.implReset()
        }

        override fun implFlush(out: CharBuffer): CoderResult {
            return super.implFlush(out)
        }


        // decode loops are not using decodeSingle/Double() for performance
        // reason.
        open fun decodeSingle(b: Int): Char {
            return b2cSB!![b]
        }

        open fun decodeDouble(b1: Int, b2: Int): Char {
            if (b1 < 0 || b1 > b2c!!.size || b2 < b2Min || b2 > b2Max) return CharsetMapping.UNMAPPABLE_DECODING
            return b2c[b1]!![b2 - b2Min]
        }
    }

    class Decoder_EBCDIC : Decoder {
        private var currentState = 0

        constructor(
            cs: Charset, b2c: Array<CharArray?>, b2cSB: CharArray, b2Min: Int, b2Max: Int, isASCIICompatible: Boolean
        ) : super(cs, b2c, b2cSB, b2Min, b2Max, isASCIICompatible)

        constructor(
            cs: Charset, b2c: Array<CharArray?>, b2cSB: CharArray, b2Min: Int, b2Max: Int
        ) : super(cs, b2c, b2cSB, b2Min, b2Max, false)

        override fun implReset() {
            currentState = SBCS
        }

        override fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa: ByteArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            val sl: Int = src.arrayOffset() + src.limit()
            val da: CharArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()

            try {
                // don't check dp/dl together here, it's possible to
                // decdoe a SO/SI without space in output buffer.
                while (sp < sl) {
                    val b1 = sa[sp].toInt() and 0xff
                    var inSize = 1
                    if (b1 == SO) {  // Shift out
                        if (currentState != SBCS) return CoderResultInternal.malformedForLength(1)
                        else currentState = DBCS
                    } else if (b1 == SI) {
                        if (currentState != DBCS) return CoderResultInternal.malformedForLength(1)
                        else currentState = SBCS
                    } else {
                        var c: Char? = null
                        if (currentState == SBCS) {
                            c = b2cSB!![b1]
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(1)
                        } else {
                            if (sl - sp < 2) return CoderResultInternal.UNDERFLOW
                            val b2 = sa[sp + 1].toInt() and 0xff
                            if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                                if (!isDoubleByte(b1, b2)) return CoderResultInternal.malformedForLength(2)
                                return CoderResultInternal.unmappableForLength(2)
                            }
                            inSize++
                        }
                        if (dl - dp < 1) return CoderResultInternal.UNDERFLOW

                        da[dp++] = c!!
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
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val b1: Int = src.getInt() and 0xff
                    var inSize = 1
                    if (b1 == SO) {  // Shift out
                        if (currentState != SBCS) return CoderResultInternal.malformedForLength(1)
                        else currentState = DBCS
                    } else if (b1 == SI) {
                        if (currentState != DBCS) return CoderResultInternal.malformedForLength(1)
                        else currentState = SBCS
                    } else {
                        var c: Char = CharsetMapping.UNMAPPABLE_DECODING
                        if (currentState == SBCS) {
                            c = b2cSB!![b1]
                            if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(1)
                        } else {
                            if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                            val b2: Int = src.getInt() and 0xff
                            if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                                if (!isDoubleByte(b1, b2)) return CoderResultInternal.malformedForLength(2)
                                return CoderResultInternal.unmappableForLength(2)
                            }
                            inSize++
                        }

                        if (dst.remaining() < 1) return CoderResultInternal.UNDERFLOW

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
            currentState = SBCS
            val repl: Char = replacement()[0]
            while (sp < sl) {
                val b1 = src[sp++].toInt() and 0xff
                if (b1 == SO) {  // Shift out
                    if (currentState != SBCS) dst[dp++] = repl
                    else currentState = DBCS
                } else if (b1 == SI) {
                    if (currentState != DBCS) dst[dp++] = repl
                    else currentState = SBCS
                } else {
                    var c: Char = CharsetMapping.UNMAPPABLE_DECODING
                    if (currentState == SBCS) {
                        c = b2cSB!![b1]
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) c = repl
                    } else {
                        if (sl == sp) {
                            c = repl
                        } else {
                            val b2 = src[sp++].toInt() and 0xff
                            if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                                c = repl
                            }
                        }
                    }
                    dst[dp++] = c
                }
            }
            return dp
        }

        companion object {
            private const val SBCS = 0
            private const val DBCS = 1
            private const val SO = 0x0e
            private const val SI = 0x0f

            // Check validity of dbcs ebcdic byte pair values
            //
            // First byte : 0x41 -- 0xFE
            // Second byte: 0x41 -- 0xFE
            // Doublebyte blank: 0x4040
            //
            // The validation implementation in "old" DBCS_IBM_EBCDIC and sun.io
            // as
            //            if ((b1 != 0x40 || b2 != 0x40) &&
            //                (b2 < 0x41 || b2 > 0xfe)) {...}
            // is not correct/complete (range check for b1)
            //
            private fun isDoubleByte(b1: Int, b2: Int): Boolean {
                return (0x41 <= b1 && b1 <= 0xfe && 0x41 <= b2 && b2 <= 0xfe) || (b1 == 0x40 && b2 == 0x40) // DBCS-HOST SPACE
            }
        }
    }

    // DBCS_ONLY
    class Decoder_DBCSONLY : Decoder {
        // always returns unmappableForLenth(2) for doublebyte_only
        override fun crMalformedOrUnmappable(b1: Int, b2: Int): CoderResult {
            return CoderResultInternal.unmappableForLength(2)
        }

        constructor(
            cs: Charset, b2c: Array<CharArray?>?, b2cSB: CharArray?, b2Min: Int, b2Max: Int,
            isASCIICompatible: Boolean
        ) : super(cs, 0.5f, 1.0f, b2c, b2cSB_UNMAPPABLE, b2Min, b2Max, isASCIICompatible)

        constructor(cs: Charset, b2c: Array<CharArray?>?, b2cSB: CharArray?, b2Min: Int, b2Max: Int) :
                super(cs, 0.5f, 1.0f, b2c, b2cSB_UNMAPPABLE, b2Min, b2Max, false)

        companion object {
            val b2cSB_UNMAPPABLE: CharArray = CharArray(0x100) { CharsetMapping.UNMAPPABLE_DECODING }
        }
    }

    // EUC_SIMPLE
    // The only thing we need to "override" is to check SS2/SS3 and
    // return "malformed" if found
    class Decoder_EUC_SIM(
        cs: Charset,
        b2c: Array<CharArray?>, b2cSB: CharArray, b2Min: Int, b2Max: Int,
        isASCIICompatible: Boolean
    ) : Decoder(cs, b2c, b2cSB, b2Min, b2Max, isASCIICompatible) {
        private val SS2 = 0x8E
        private val SS3 = 0x8F

        // No support provided for G2/G3 for SimpleEUC
        override fun crMalformedOrUnderFlow(b: Int): CoderResult {
            if (b == SS2 || b == SS3) return CoderResultInternal.malformedForLength(1)
            return CoderResultInternal.UNDERFLOW
        }

        override fun crMalformedOrUnmappable(b1: Int, b2: Int): CoderResult {
            if (b1 == SS2 || b1 == SS3) return CoderResultInternal.malformedForLength(1)
            return CoderResultInternal.unmappableForLength(2)
        }

        override fun decode(src: ByteArray, sp: Int, len: Int, dst: CharArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            val repl: Char = replacement()[0]
            while (sp < sl) {
                val b1 = src[sp++].toInt() and 0xff
                var c: Char = b2cSB!![b1]
                if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                    if (sp < sl) {
                        val b2 = src[sp++].toInt() and 0xff
                        if (b2 < b2Min || b2 > b2Max || (b2c!![b1]!![b2 - b2Min].also { c = it }) == CharsetMapping.UNMAPPABLE_DECODING) {
                            if (b1 == SS2 || b1 == SS3) {
                                sp--
                            }
                            c = repl
                        }
                    } else {
                        c = repl
                    }
                }
                dst[dp++] = c
            }
            return dp
        }
    }

    open class Encoder : CharsetEncoder, ArrayEncoder {
        protected val MAX_SINGLEBYTE: Int = 0xff
        private val c2b: CharArray?
        private val c2bIndex: CharArray?
        protected var _sgp: Surrogate.Parser? = null

        private val _isASCIICompatible: Boolean

        constructor(cs: Charset, c2b: CharArray?, c2bIndex: CharArray?, isASCIICompatible: Boolean = false) : super(cs, 2.0f, 2.0f) {
            this.c2b = c2b
            this.c2bIndex = c2bIndex
            this._isASCIICompatible = isASCIICompatible
        }

        constructor(
            cs: Charset, avg: Float, max: Float, repl: ByteArray, c2b: CharArray, c2bIndex: CharArray,
            isASCIICompatible: Boolean
        ) : super(cs, avg, max, repl) {
            this.c2b = c2b
            this.c2bIndex = c2bIndex
            this._isASCIICompatible = isASCIICompatible
        }

        override fun isASCIICompatible(): Boolean {
            return _isASCIICompatible
        }

        override fun canEncode(c: Char): Boolean {
            return encodeChar(c) != CharsetMapping.UNMAPPABLE_ENCODING
        }

        protected fun sgp(): Surrogate.Parser {
            if (_sgp == null) _sgp = Surrogate.Parser()
            return _sgp!!
        }

        protected open fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa: CharArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            val sl: Int = src.arrayOffset() + src.limit()

            val da: ByteArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()

            try {
                if (_isASCIICompatible) {
                    val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(dl - dp, sl - sp))
                    sp += n
                    dp += n
                }
                while (sp < sl) {
                    val c = sa[sp]
                    val bb = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            if (sgp().parse(c, sa, sp, sl) < 0) return _sgp!!.error()
                            return _sgp!!.unmappableResult()
                        }
                        return CoderResultInternal.unmappableForLength(1)
                    }

                    if (bb > MAX_SINGLEBYTE) {    // DoubleByte
                        if (dl - dp < 2) return CoderResultInternal.UNDERFLOW
                        da[dp++] = (bb shr 8).toByte()
                        da[dp++] = bb.toByte()
                    } else {                      // SingleByte
                        if (dl - dp < 1) return CoderResultInternal.UNDERFLOW
                        da[dp++] = bb.toByte()
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
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    val bb = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            if (sgp().parse(c, src) < 0) return _sgp!!.error()
                            return _sgp!!.unmappableResult()
                        }
                        return CoderResultInternal.unmappableForLength(1)
                    }
                    if (bb > MAX_SINGLEBYTE) {  // DoubleByte
                        if (dst.remaining() < 2) return CoderResultInternal.UNDERFLOW
                        dst.put((bb shr 8).toByte())
                        dst.put((bb).toByte())
                    } else {
                        if (dst.remaining() < 1) return CoderResultInternal.UNDERFLOW
                        dst.put(bb.toByte())
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

        internal open var repl: ByteArray = replacement()

        override fun implReplaceWith(newReplacement: ByteArray) {
            repl = newReplacement
        }

        override fun encode(src: CharArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            if (_isASCIICompatible) {
                val n: Int = JLA.encodeASCII(src, sp, dst, dp, len)
                sp += n
                dp += n
            }
            while (sp < sl) {
                val c = src[sp++]
                val bb = encodeChar(c)
                if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                    if (Character.isHighSurrogate(c) && sp < sl &&
                        Character.isLowSurrogate(src[sp])
                    ) {
                        sp++
                    }
                    dst[dp++] = repl[0]
                    if (repl.size > 1) dst[dp++] = repl[1]
                    continue
                } //else

                if (bb > MAX_SINGLEBYTE) { // DoubleByte
                    dst[dp++] = (bb shr 8).toByte()
                    dst[dp++] = bb.toByte()
                } else {                          // SingleByte
                    dst[dp++] = bb.toByte()
                }
            }
            return dp
        }

        override fun encodeFromLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            while (sp < sl) {
                val c = (src[sp++].toInt() and 0xff).toChar()
                val bb = encodeChar(c)
                if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                    // no surrogate pair in latin1 string
                    dst[dp++] = repl[0]
                    if (repl.size > 1) {
                        dst[dp++] = repl[1]
                    }
                    continue
                } //else

                if (bb > MAX_SINGLEBYTE) { // DoubleByte
                    dst[dp++] = (bb shr 8).toByte()
                    dst[dp++] = bb.toByte()
                } else {                   // SingleByte
                    dst[dp++] = bb.toByte()
                }
            }
            return dp
        }

        open fun encodeChar(ch: Char): Int {
            return c2b!![c2bIndex!![ch.code shr 8].code + (ch.code and 0xff)].code
        }

        companion object {
            // init the c2b and c2bIndex tables from b2c.
            fun initC2B(
                b2c: Array<String?>, b2cSB: String?, b2cNR: String?, c2bNR: String?,
                b2Min: Int, b2Max: Int,
                c2b: CharArray, c2bIndex: CharArray
            ) {
                c2b.fill(CharsetMapping.UNMAPPABLE_ENCODING_CHAR)
                var off = 0x100

                val b2c_ca = arrayOfNulls<CharArray>(b2c.size)
                var b2cSB_ca: CharArray? = null
                if (b2cSB != null) b2cSB_ca = b2cSB.toCharArray()

                for (i in b2c.indices) {
                    if (b2c[i] == null) continue
                    b2c_ca[i] = b2c[i]!!.toCharArray()
                }

                if (b2cNR != null) {
                    var j = 0
                    while (j < b2cNR.length) {
                        val b: Char = b2cNR[j++]
                        val c: Char = b2cNR[j++]
                        if (b.code < 0x100 && b2cSB_ca != null) {
                            if (b2cSB_ca[b.code] == c) b2cSB_ca[b.code] = CharsetMapping.UNMAPPABLE_DECODING
                        } else {
                            if (b2c_ca[b.code shr 8]!![(b.code and 0xff) - b2Min] == c) b2c_ca[b.code shr 8]!![(b.code and 0xff) - b2Min] =
                                CharsetMapping.UNMAPPABLE_DECODING
                        }
                    }
                }

                if (b2cSB_ca != null) {      // SingleByte
                    for (b in b2cSB_ca.indices) {
                        val c = b2cSB_ca[b]
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) continue
                        var index = c2bIndex[c.code shr 8].code
                        if (index == 0) {
                            index = off
                            off += 0x100
                            c2bIndex[c.code shr 8] = index.toChar()
                        }
                        c2b[index + (c.code and 0xff)] = b.toChar()
                    }
                }

                for (b1 in b2c.indices) {  // DoubleByte
                    val db = b2c_ca[b1]
                    if (db == null) continue
                    for (b2 in b2Min..b2Max) {
                        val c = db[b2 - b2Min]
                        if (c == CharsetMapping.UNMAPPABLE_DECODING) continue
                        var index = c2bIndex[c.code shr 8].code
                        if (index == 0) {
                            index = off
                            off += 0x100
                            c2bIndex[c.code shr 8] = index.toChar()
                        }
                        c2b[index + (c.code and 0xff)] = ((b1 shl 8) or b2).toChar()
                    }
                }

                if (c2bNR != null) {
                    // add c->b only nr entries
                    var i = 0
                    while (i < c2bNR.length) {
                        val b: Char = c2bNR[i]
                        val c: Char = c2bNR[i + 1]
                        var index = (c.code shr 8)
                        if (c2bIndex[index].code == 0) {
                            c2bIndex[index] = off.toChar()
                            off += 0x100
                        }
                        index = c2bIndex[index].code + (c.code and 0xff)
                        c2b[index] = b
                        i += 2
                    }
                }
            }
        }
    }

    open class Encoder_DBCSONLY(cs: Charset, repl: ByteArray, c2b: CharArray, c2bIndex: CharArray, isASCIICompatible: Boolean) :
        Encoder(cs, 2.0f, 2.0f, repl, c2b, c2bIndex, isASCIICompatible) {
        override fun encodeChar(ch: Char): Int {
            val bb: Int = super.encodeChar(ch)
            if (bb <= MAX_SINGLEBYTE) return CharsetMapping.UNMAPPABLE_ENCODING
            return bb
        }
    }

    open class Encoder_EBCDIC(
        cs: Charset, c2b: CharArray, c2bIndex: CharArray,
        isASCIICompatible: Boolean
    ) : Encoder(cs, 4.0f, 5.0f, byteArrayOf(0x6f.toByte()), c2b, c2bIndex, isASCIICompatible) {
        private var currentState: Int = SBCS

        override fun implReset() {
            currentState = SBCS
        }

        override fun implFlush(out: ByteBuffer): CoderResult {
            if (currentState == DBCS) {
                if (out.remaining() < 1) return CoderResultInternal.UNDERFLOW
                out.put(SI)
            }
            implReset()
            return CoderResultInternal.UNDERFLOW
        }

        override fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa: CharArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            val sl: Int = src.arrayOffset() + src.limit()
            val da: ByteArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    val bb: Int = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            if (sgp().parse(c, sa, sp, sl) < 0) return _sgp!!.error()
                            return _sgp!!.unmappableResult()
                        }
                        return CoderResultInternal.unmappableForLength(1)
                    }
                    if (bb > MAX_SINGLEBYTE) {  // DoubleByte
                        if (currentState == SBCS) {
                            if (dl - dp < 1) return CoderResultInternal.UNDERFLOW
                            currentState = DBCS
                            da[dp++] = SO
                        }
                        if (dl - dp < 2) return CoderResultInternal.UNDERFLOW
                        da[dp++] = (bb shr 8).toByte()
                        da[dp++] = bb.toByte()
                    } else {                    // SingleByte
                        if (currentState == DBCS) {
                            if (dl - dp < 1) return CoderResultInternal.UNDERFLOW
                            currentState = SBCS
                            da[dp++] = SI
                        }
                        if (dl - dp < 1) return CoderResultInternal.UNDERFLOW
                        da[dp++] = bb.toByte()
                    }
                    sp++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        protected fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    val bb: Int = encodeChar(c)
                    if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            if (sgp().parse(c, src) < 0) return _sgp!!.error()
                            return _sgp!!.unmappableResult()
                        }
                        return CoderResultInternal.unmappableForLength(1)
                    }
                    if (bb > MAX_SINGLEBYTE) {  // DoubleByte
                        if (currentState == SBCS) {
                            if (dst.remaining() < 1) return CoderResultInternal.UNDERFLOW
                            currentState = DBCS
                            dst.put(SO)
                        }
                        if (dst.remaining() < 2) return CoderResultInternal.UNDERFLOW
                        dst.put((bb shr 8).toByte())
                        dst.put((bb).toByte())
                    } else {                  // Single-byte
                        if (currentState == DBCS) {
                            if (dst.remaining() < 1) return CoderResultInternal.UNDERFLOW
                            currentState = SBCS
                            dst.put(SI)
                        }
                        if (dst.remaining() < 1) return CoderResultInternal.UNDERFLOW
                        dst.put(bb.toByte())
                    }
                    mark++
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun encode(src: CharArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            while (sp < sl) {
                val c = src[sp++]
                val bb: Int = encodeChar(c)

                if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                    if (Character.isHighSurrogate(c) && sp < sl &&
                        Character.isLowSurrogate(src[sp])
                    ) {
                        sp++
                    }
                    dst[dp++] = repl[0]
                    if (repl.size > 1) dst[dp++] = repl[1]
                    continue
                } //else

                if (bb > MAX_SINGLEBYTE) {           // DoubleByte
                    if (currentState == SBCS) {
                        currentState = DBCS
                        dst[dp++] = SO
                    }
                    dst[dp++] = (bb shr 8).toByte()
                    dst[dp++] = bb.toByte()
                } else {                             // SingleByte
                    if (currentState == DBCS) {
                        currentState = SBCS
                        dst[dp++] = SI
                    }
                    dst[dp++] = bb.toByte()
                }
            }

            if (currentState == DBCS) {
                currentState = SBCS
                dst[dp++] = SI
            }
            return dp
        }

        override fun encodeFromLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl = sp + len
            while (sp < sl) {
                val c = (src[sp++].toInt() and 0xff).toChar()
                val bb: Int = encodeChar(c)
                if (bb == CharsetMapping.UNMAPPABLE_ENCODING) {
                    // no surrogate pair in latin1 string
                    dst[dp++] = repl[0]
                    if (repl.size > 1) dst[dp++] = repl[1]
                    continue
                } //else

                if (bb > MAX_SINGLEBYTE) {           // DoubleByte
                    if (currentState == SBCS) {
                        currentState = DBCS
                        dst[dp++] = SO
                    }
                    dst[dp++] = (bb shr 8).toByte()
                    dst[dp++] = bb.toByte()
                } else {                             // SingleByte
                    if (currentState == DBCS) {
                        currentState = SBCS
                        dst[dp++] = SI
                    }
                    dst[dp++] = bb.toByte()
                }
            }
            if (currentState == DBCS) {
                currentState = SBCS
                dst[dp++] = SI
            }
            return dp
        }

        companion object {
            const val SBCS: Int = 0
            const val DBCS: Int = 1
            const val SO: Byte = 0x0e
            const val SI: Byte = 0x0f
        }
    }

    // EUC_SIMPLE
    class Encoder_EUC_SIM(
        cs: Charset, c2b: CharArray?, c2bIndex: CharArray?,
        isASCIICompatible: Boolean
    ) : Encoder(cs, c2b, c2bIndex, isASCIICompatible)
}