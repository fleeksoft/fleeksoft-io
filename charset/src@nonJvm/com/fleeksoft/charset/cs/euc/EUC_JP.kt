package com.fleeksoft.charset.cs.euc

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.*
import com.fleeksoft.charset.cs.jis.JIS_X_0201
import com.fleeksoft.charset.cs.jis.JIS_X_0208
import com.fleeksoft.charset.cs.jis.JIS_X_0212
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.min

class EUC_JP : Charset("EUC-JP", null) {
    companion object {
        val INSTANCE = EUC_JP()
    }

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is JIS_X_0201)
                || (cs is JIS_X_0208)
                || (cs is JIS_X_0212)
                || (cs is EUC_JP))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    open class Decoder protected constructor(
        cs: Charset, avgCpb: Float, maxCpb: Float,
        private val dec0201: SingleByte.Decoder,
        private val dec0208: DoubleByte.Decoder,
        private val dec0212: DoubleByte.Decoder?
    ) : CharsetDecoder(cs, avgCpb, maxCpb), DelegatableDecoder {

        constructor(cs: Charset) : this(cs, 0.5f, 1.0f, DEC0201, DEC0208, DEC0212)


        protected open fun decodeDouble(byte1: Int, byte2: Int): Char {
            if (byte1 == 0x8e) {
                if (byte2 < 0x80) return CharsetMapping.UNMAPPABLE_DECODING
                return dec0201.decode(byte2)
            }
            return dec0208.decodeDouble(byte1 - 0x80, byte2 - 0x80)
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

                    if ((b1 and 0x80) == 0) {
                        outputChar = b1.toChar()
                    } else {                        // Multibyte char
                        if (b1 == 0x8f) {           // JIS0212
                            if (sp + 3 > sl) return CoderResultInternal.UNDERFLOW
                            b1 = sa[sp + 1].toInt() and 0xff
                            b2 = sa[sp + 2].toInt() and 0xff
                            inputSize += 2
                            if (dec0212 == null)  // JIS02012 not supported
                                return CoderResultInternal.unmappableForLength(inputSize)
                            outputChar = dec0212.decodeDouble(b1 - 0x80, b2 - 0x80)
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
                    if ((b1 and 0x80) == 0) {
                        outputChar = b1.toChar()
                    } else {                         // Multibyte char
                        if (b1 == 0x8f) {   // JIS0212
                            if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                            b1 = src.getInt() and 0xff
                            b2 = src.getInt() and 0xff
                            inputSize += 2
                            if (dec0212 == null)  // JIS02012 not supported
                                return CoderResultInternal.unmappableForLength(inputSize)
                            outputChar = dec0212.decodeDouble(b1 - 0x80, b2 - 0x80)
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
        }
    }


    open class Encoder protected constructor(
        cs: Charset, avgBpc: Float, maxBpc: Float,
        enc0201: SingleByte.Encoder,
        enc0208: DoubleByte.Encoder,
        enc0212: DoubleByte.Encoder?
    ) : CharsetEncoder(cs, avgBpc, maxBpc) {
        private val sgp: Surrogate.Parser = Surrogate.Parser()


        private val enc0201: SingleByte.Encoder
        private val enc0208: DoubleByte.Encoder
        private val enc0212: DoubleByte.Encoder?

        constructor(cs: Charset) : this(cs, 3.0f, 3.0f, ENC0201, ENC0208, ENC0212)

        init {
            this.enc0201 = enc0201
            this.enc0208 = enc0208
            this.enc0212 = enc0212
        }

        override fun canEncode(c: Char): Boolean {
            val encodedBytes = ByteArray(3)
            return encodeSingle(c, encodedBytes) != 0 ||
                    encodeDouble(c) != CharsetMapping.UNMAPPABLE_ENCODING
        }

        protected fun encodeSingle(inputChar: Char, outputByte: ByteArray): Int {
            val b: Int = enc0201.encode(inputChar)
            if (b == CharsetMapping.UNMAPPABLE_ENCODING) return 0
            if (b >= 0 && b < 128) {
                outputByte[0] = b.toByte()
                return 1
            }
            outputByte[0] = 0x8e.toByte()
            outputByte[1] = b.toByte()
            return 2
        }

        protected open fun encodeDouble(ch: Char): Int {
            var b: Int = enc0208.encodeChar(ch)
            if (b != CharsetMapping.UNMAPPABLE_ENCODING) return b + 0x8080
            if (enc0212 != null) {
                b = enc0212.encodeChar(ch)
                if (b != CharsetMapping.UNMAPPABLE_ENCODING) b += 0x8F8080
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
                if (enc0201.isASCIICompatible()) {
                    val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(dl - dp, sl - sp))
                    sp += n
                    dp += n
                }
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

            val ENC0212: DoubleByte.Encoder? = JIS_X_0212().newEncoder() as DoubleByte.Encoder?
        }
    }
}
