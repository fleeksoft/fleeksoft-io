package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.Buffer
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.min

object SingleByte {
    private fun withResult(cr: CoderResult, src: Buffer, sp: Int, dst: Buffer, dp: Int): CoderResult {
        src.position(sp - src.arrayOffset())
        dst.position(dp - dst.arrayOffset())
        return cr
    }

    // init the c2b and c2bIndex tables from b2c.
    fun initC2B(
        b2c: CharArray, c2bNR: CharArray?,
        c2b: CharArray, c2bIndex: CharArray
    ) {
        for (i in c2bIndex.indices) c2bIndex[i] = CharsetMapping.UNMAPPABLE_ENCODING_CHAR
        for (i in c2b.indices) c2b[i] = CharsetMapping.UNMAPPABLE_ENCODING_CHAR
        var off = 0
        for (i in b2c.indices) {
            val c = b2c[i]
            if (c == CharsetMapping.UNMAPPABLE_DECODING) continue
            var index = (c.code shr 8)
            if (c2bIndex[index] == CharsetMapping.UNMAPPABLE_ENCODING_CHAR) {
                c2bIndex[index] = off.toChar()
                off += 0x100
            }
            index = c2bIndex[index].code + (c.code and 0xff)
            c2b[index] = (if (i >= 0x80) (i - 0x80) else (i + 0x80)).toChar()
        }
        if (c2bNR != null) {
            // c-->b nr entries
            var i = 0
            while (i < c2bNR.size) {
                val b = c2bNR[i++]
                val c = c2bNR[i++]
                var index = (c.code shr 8)
                if (c2bIndex[index] == CharsetMapping.UNMAPPABLE_ENCODING_CHAR) {
                    c2bIndex[index] = off.toChar()
                    off += 0x100
                }
                index = c2bIndex[index].code + (c.code and 0xff)
                c2b[index] = b
            }
        }
    }

    open class Decoder : CharsetDecoder, ArrayDecoder {
        private val b2c: CharArray
        private val isASCIICompatible: Boolean
        private val isLatin1Decodable: Boolean

        constructor(cs: Charset, b2c: CharArray) : super(cs, 1.0f, 1.0f) {
            this.b2c = b2c
            this.isASCIICompatible = false
            this.isLatin1Decodable = false
        }

        constructor(cs: Charset, b2c: CharArray, isASCIICompatible: Boolean) : super(cs, 1.0f, 1.0f) {
            this.b2c = b2c
            this.isASCIICompatible = isASCIICompatible
            this.isLatin1Decodable = false
        }

        constructor(cs: Charset, b2c: CharArray, isASCIICompatible: Boolean, isLatin1Decodable: Boolean) : super(cs, 1.0f, 1.0f) {
            this.b2c = b2c
            this.isASCIICompatible = isASCIICompatible
            this.isLatin1Decodable = isLatin1Decodable
        }

        private fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa: ByteArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            var sl: Int = src.arrayOffset() + src.limit()

            val da: CharArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()

            var cr: CoderResult = CoderResultInternal.UNDERFLOW
            if ((dl - dp) < (sl - sp)) {
                sl = sp + (dl - dp)
                cr = CoderResultInternal.OVERFLOW
            }

            if (isASCIICompatible) {
                val n: Int = JLA.decodeASCII(sa, sp, da, dp, min(dl - dp, sl - sp))
                sp += n
                dp += n
            }
            while (sp < sl) {
                val c = decode(sa[sp].toInt())
                if (c == CharsetMapping.UNMAPPABLE_DECODING) {
                    return withResult(
                        CoderResultInternal.unmappableForLength(1),
                        src, sp, dst, dp
                    )
                }
                da[dp++] = c
                sp++
            }
            return withResult(cr, src, sp, dst, dp)
        }

        private fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c = decode(src.getInt())
                    if (c == CharsetMapping.UNMAPPABLE_DECODING) return CoderResultInternal.unmappableForLength(1)
                    if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                    dst.put(c)
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

        fun decode(b: Int): Char {
            return b2c[b + 128]
        }

        private var repl = '\uFFFD'

        override fun implReplaceWith(newReplacement: String) {
            repl = newReplacement[0]
        }

        override fun decodeToLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var len = len
            if (len > dst.size) len = dst.size

            var dp = 0
            while (dp < len) {
                dst[dp++] = decode(src[sp++].toInt()).code.toByte()
            }
            return dp
        }

        override fun decode(src: ByteArray, sp: Int, len: Int, dst: CharArray): Int {
            var sp = sp
            var len = len
            if (len > dst.size) len = dst.size
            var dp = 0
            while (dp < len) {
                dst[dp] = decode(src[sp++].toInt())
                if (dst[dp] == CharsetMapping.UNMAPPABLE_DECODING) {
                    dst[dp] = repl
                }
                dp++
            }
            return dp
        }

        override fun isASCIICompatible(): Boolean {
            return isASCIICompatible
        }

        override fun isLatin1Decodable(): Boolean {
            return isLatin1Decodable
        }
    }

    class Encoder(cs: Charset, private val c2b: CharArray, private val c2bIndex: CharArray, private val isASCIICompatible: Boolean) :
        CharsetEncoder(cs, 1.0f, 1.0f), ArrayEncoder {
        private var sgp: Surrogate.Parser? = null

        override fun canEncode(c: Char): Boolean {
            return encode(c) != CharsetMapping.UNMAPPABLE_ENCODING
        }

        override fun isLegalReplacement(repl: ByteArray): Boolean {
            return ((repl.size == 1 && repl[0] == '?'.code.toByte()) ||
                    super.isLegalReplacement(repl))
        }

        private fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa: CharArray = src.array()
            var sp: Int = src.arrayOffset() + src.position()
            val sl: Int = src.arrayOffset() + src.limit()

            val da: ByteArray = dst.array()
            var dp: Int = dst.arrayOffset() + dst.position()
            val dl: Int = dst.arrayOffset() + dst.limit()
            var len: Int = min(dl - dp, sl - sp)

            if (isASCIICompatible) {
                val n: Int = JLA.encodeASCII(sa, sp, da, dp, len)
                sp += n
                dp += n
                len -= n
            }
            while (len-- > 0) {
                val c = sa[sp]
                val b = encode(c)
                if (b == CharsetMapping.UNMAPPABLE_ENCODING) {
                    if (Character.isSurrogate(c)) {
                        if (sgp == null) sgp = Surrogate.Parser()
                        if (sgp!!.parse(c, sa, sp, sl) < 0) {
                            return withResult(sgp!!.error(), src, sp, dst, dp)
                        }
                        return withResult(sgp!!.unmappableResult(), src, sp, dst, dp)
                    }
                    return withResult(
                        CoderResultInternal.unmappableForLength(1),
                        src, sp, dst, dp
                    )
                }
                da[dp++] = b.toByte()
                sp++
            }
            return withResult(
                if (sp < sl) CoderResultInternal.OVERFLOW else CoderResultInternal.UNDERFLOW,
                src, sp, dst, dp
            )
        }

        private fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark: Int = src.position()
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    val b = encode(c)
                    if (b == CharsetMapping.UNMAPPABLE_ENCODING) {
                        if (Character.isSurrogate(c)) {
                            if (sgp == null) sgp = Surrogate.Parser()
                            if (sgp!!.parse(c, src) < 0) return sgp!!.error()
                            return sgp!!.unmappableResult()
                        }
                        return CoderResultInternal.unmappableForLength(1)
                    }
                    if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                    dst.put(b.toByte())
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

        fun encode(ch: Char): Int {
            val index: Char = c2bIndex[ch.code shr 8]
            if (index == CharsetMapping.UNMAPPABLE_ENCODING_CHAR) return CharsetMapping.UNMAPPABLE_ENCODING
            return c2b[index.code + (ch.code and 0xff)].code
        }

        private var repl = '?'.code.toByte()

        override fun implReplaceWith(newReplacement: ByteArray) {
            repl = newReplacement[0]
        }

        override fun encode(src: CharArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var len = len
            var dp = 0
            var sl: Int = sp + min(len, dst.size)
            while (sp < sl) {
                val c = src[sp++]
                val b = encode(c)
                if (b != CharsetMapping.UNMAPPABLE_ENCODING) {
                    dst[dp++] = b.toByte()
                    continue
                }
                if (Character.isHighSurrogate(c) && sp < sl &&
                    Character.isLowSurrogate(src[sp])
                ) {
                    if (len > dst.size) {
                        sl++
                        len--
                    }
                    sp++
                }
                dst[dp++] = repl
            }
            return dp
        }

        override fun encodeFromLatin1(src: ByteArray, sp: Int, len: Int, dst: ByteArray): Int {
            var sp = sp
            var dp = 0
            val sl: Int = sp + min(len, dst.size)
            while (sp < sl) {
                val c = (src[sp++].toInt() and 0xff).toChar()
                val b = encode(c)
                if (b == CharsetMapping.UNMAPPABLE_ENCODING) {
                    dst[dp++] = repl
                } else {
                    dst[dp++] = b.toByte()
                }
            }
            return dp
        }

        override fun isASCIICompatible(): Boolean {
            return isASCIICompatible
        }
    }
}