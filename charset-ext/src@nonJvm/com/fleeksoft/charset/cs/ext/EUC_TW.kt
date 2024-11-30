package com.fleeksoft.charset.cs.ext


import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.CharsetMapping
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.max


/*
       (1) EUC_TW
       Second byte of EUC_TW for cs2 is in range of
       0xA1-0xB0 for plane 1-16. According to CJKV /163,
       plane1 is coded in both cs1 and cs2. This impl
       however does not decode the codepoints of plane1
       in cs2, so only p2-p7 and p15 are supported in cs2.

       Plane2  0xA2;
       Plane3  0xA3;
       Plane4  0xA4;
       Plane5  0xA5;
       Plane6  0xA6;
       Plane7  0xA7;
       Plane15 0xAF;

       (2) Mapping
       The fact that all supplementary characters encoded in EUC_TW are
       in 0x2xxxx range gives us the room to optimize the data tables.

       Decoding:
       (1) save the lower 16-bit value of all codepoints of b->c mapping
           in a String array table  String[plane] b2c.
       (2) save "codepoint is supplementary" info (one bit) in a
           byte[] b2cIsSupp, so 8 codepoints (same codepoint value, different
           plane No) share one byte.

       Encoding:
       (1)c->b mappings are stored in
          char[]c2b/char[]c2bIndex
          char[]c2bSupp/char[]c2bIndexsupp  (indexed by lower 16-bit
       (2)byte[] c2bPlane stores the "plane info" of each euc-tw codepoints,
          BMP and Supp share the low/high 4 bits of one byte.

       Mapping tables are stored separated in EUC_TWMapping, which
       is generated by tool.
     */
class EUC_TW : Charset("x-EUC-TW", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs.name() == "US-ASCII")
                || (cs is EUC_TW))
    }

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    class Decoder(cs: Charset) : CharsetDecoder(cs, 2.0f, 2.0f) {
        var c1: CharArray = CharArray(1)
        var c2: CharArray = CharArray(2)
        fun toUnicode(b1: Int, b2: Int, p: Int): CharArray? {
            return decode(b1, b2, p, c1, c2)
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
            try {
                while (sp < sl) {
                    var byte1 = sa[sp].toInt() and 0xff
                    if (byte1 == SS2) { // Codeset 2  G2
                        if (sl - sp < 4) return CoderResultInternal.UNDERFLOW
                        val cnsPlane = cnspToIndex[sa[sp + 1].toInt() and 0xff].toInt()
                        if (cnsPlane < 0) return CoderResultInternal.malformedForLength(2)
                        byte1 = sa[sp + 2].toInt() and 0xff
                        val byte2 = sa[sp + 3].toInt() and 0xff
                        val cc = toUnicode(byte1, byte2, cnsPlane)
                        if (cc == null) {
                            if (!isLegalDB(byte1) || !isLegalDB(byte2)) return CoderResultInternal.malformedForLength(4)
                            return CoderResultInternal.unmappableForLength(4)
                        }
                        if (dl - dp < cc.size) return CoderResultInternal.OVERFLOW
                        if (cc.size == 1) {
                            da[dp++] = cc[0]
                        } else {
                            da[dp++] = cc[0]
                            da[dp++] = cc[1]
                        }
                        sp += 4
                    } else if (byte1 < 0x80) {  // ASCII      G0
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = byte1.toChar()
                        sp++
                    } else {                    // Codeset 1  G1
                        if (sl - sp < 2) return CoderResultInternal.UNDERFLOW
                        val byte2 = sa[sp + 1].toInt() and 0xff
                        val cc = toUnicode(byte1, byte2, 0)
                        if (cc == null) {
                            if (!isLegalDB(byte1) || !isLegalDB(byte2)) return CoderResultInternal.malformedForLength(1)
                            return CoderResultInternal.unmappableForLength(2)
                        }
                        if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                        da[dp++] = cc[0]
                        sp += 2
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        private fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()
            try {
                while (src.hasRemaining()) {
                    var byte1 = src.getInt() and 0xff
                    if (byte1 == SS2) {            // Codeset 2  G2
                        if (src.remaining() < 3) return CoderResultInternal.UNDERFLOW
                        val cnsPlane = cnspToIndex[src.getInt() and 0xff].toInt()
                        if (cnsPlane < 0) return CoderResultInternal.malformedForLength(2)
                        byte1 = src.getInt() and 0xff
                        val byte2 = src.getInt() and 0xff
                        val cc = toUnicode(byte1, byte2, cnsPlane)
                        if (cc == null) {
                            if (!isLegalDB(byte1) || !isLegalDB(byte2)) return CoderResultInternal.malformedForLength(4)
                            return CoderResultInternal.unmappableForLength(4)
                        }
                        if (dst.remaining() < cc.size) return CoderResultInternal.OVERFLOW
                        if (cc.size == 1) {
                            dst.put(cc[0])
                        } else {
                            dst.put(cc[0])
                            dst.put(cc[1])
                        }
                        mark += 4
                    } else if (byte1 < 0x80) {        // ASCII      G0
                        if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                        dst.put(byte1.toChar())
                        mark++
                    } else {                          // Codeset 1  G1
                        if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                        val byte2 = src.getInt() and 0xff
                        val cc = toUnicode(byte1, byte2, 0)
                        if (cc == null) {
                            if (!isLegalDB(byte1) || !isLegalDB(byte2)) return CoderResultInternal.malformedForLength(1)
                            return CoderResultInternal.unmappableForLength(2)
                        }
                        if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                        dst.put(cc[0])
                        mark += 2
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return decodeArrayLoop(src, dst)
        }

        companion object {
            val b2c: Array<String> = EUC_TWMapping.b2c
            val b1Min: Int = EUC_TWMapping.b1Min
            val b1Max: Int = EUC_TWMapping.b1Max
            val b2Min: Int = EUC_TWMapping.b2Min
            val b2Max: Int = EUC_TWMapping.b2Max
            val dbSegSize: Int = b2Max - b2Min + 1
            val b2cIsSupp: ByteArray

            // adjust from cns planeNo to the plane index of b2c
            val cnspToIndex: ByteArray = ByteArray(0x100) { (-1).toByte() }

            init {
                cnspToIndex[0xa2] = 1
                cnspToIndex[0xa3] = 2
                cnspToIndex[0xa4] = 3
                cnspToIndex[0xa5] = 4
                cnspToIndex[0xa6] = 5
                cnspToIndex[0xa7] = 6
                cnspToIndex[0xaf] = 7
            }

            //static final BitSet b2cIsSupp;
            init {
                val b2cIsSuppStr: String = EUC_TWMapping.b2cIsSuppStr
                // work on a local copy is much faster than operate
                // directly on b2cIsSupp
                val flag = ByteArray(b2cIsSuppStr.length shl 1)
                var off = 0
                for (i in b2cIsSuppStr.indices) {
                    val c: Char = b2cIsSuppStr[i]
                    flag[off++] = (c.code shr 8).toByte()
                    flag[off++] = (c.code and 0xff).toByte()
                }
                b2cIsSupp = flag
            }

            fun isLegalDB(b: Int): Boolean {
                return b >= b1Min && b <= b1Max
            }

            fun decodeSingleOrReplace(b1: Int, b2: Int, p: Int, replace: Char): Char {
                if (b1 < b1Min || b1 > b1Max || b2 < b2Min || b2 > b2Max) return replace
                val index = (b1 - b1Min) * dbSegSize + b2 - b2Min
                val c: Char = b2c[p][index]
                if (c == CharsetMapping.UNMAPPABLE_DECODING) return replace
                if ((b2cIsSupp[index].toInt() and (1 shl p)) == 0) {
                    return c
                }
                return replace
            }

            fun decode(b1: Int, b2: Int, p: Int, c1: CharArray, c2: CharArray): CharArray? {
                if (b1 < b1Min || b1 > b1Max || b2 < b2Min || b2 > b2Max) return null
                val index = (b1 - b1Min) * dbSegSize + b2 - b2Min
                val c: Char = b2c[p][index]
                if (c == CharsetMapping.UNMAPPABLE_DECODING) return null
                if ((b2cIsSupp[index].toInt() and (1 shl p)) == 0) {
                    c1[0] = c
                    return c1
                } else {
                    c2[0] = Character.highSurrogate(0x20000 + c.code)
                    c2[1] = Character.lowSurrogate(0x20000 + c.code)
                    return c2
                }
            }
        }
    }

    class Encoder(cs: Charset) : CharsetEncoder(cs, 4.0f, 4.0f) {
        private val bb = ByteArray(4)

        override fun canEncode(c: Char): Boolean {
            return (c <= '\u007f' || toEUC(c, bb) != -1)
        }

        override fun canEncode(cs: CharSequence): Boolean {
            var i = 0
            while (i < cs.length) {
                val c: Char = cs[i++]
                if (Character.isHighSurrogate(c)) {
                    if (i == cs.length) return false
                    val low: Char = cs[i++]
                    if (!Character.isLowSurrogate(low) || toEUC(c, low, bb) == -1) return false
                } else if (!canEncode(c)) {
                    return false
                }
            }
            return true
        }

        fun toEUC(hi: Char, low: Char, bb: ByteArray): Int {
            return encode(hi, low, bb)
        }

        fun toEUC(c: Char, bb: ByteArray): Int {
            return encode(c, bb)
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

            var inSize: Int
            var outSize: Int

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    inSize = 1
                    if (c.code < 0x80) {  // ASCII
                        bb[0] = c.code.toByte()
                        outSize = 1
                    } else {
                        outSize = toEUC(c, bb)
                        if (outSize == -1) {
                            // to check surrogates only after BMP failed
                            // has the benefit of improving the BMP encoding
                            // 10% faster, with the price of the slowdown of
                            // supplementary character encoding. given the use
                            // of supplementary characters is really rare, this
                            // is something worth doing.
                            if (Character.isHighSurrogate(c)) {
                                if ((sp + 1) == sl) return CoderResultInternal.UNDERFLOW
                                if (!Character.isLowSurrogate(sa[sp + 1])) return CoderResultInternal.malformedForLength(1)
                                outSize = toEUC(c, sa[sp + 1], bb)
                                inSize = 2
                            } else if (Character.isLowSurrogate(c)) {
                                return CoderResultInternal.malformedForLength(1)
                            }
                        }
                    }
                    if (outSize == -1) return CoderResultInternal.unmappableForLength(inSize)
                    if (dl - dp < outSize) return CoderResultInternal.OVERFLOW
                    for (i in 0..<outSize) da[dp++] = bb[i]
                    sp += inSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        private fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var outSize: Int
            var inSize: Int
            var mark = src.position()

            try {
                while (src.hasRemaining()) {
                    inSize = 1
                    val c = src.get()
                    if (c.code < 0x80) {   // ASCII
                        outSize = 1
                        bb[0] = c.code.toByte()
                    } else {
                        outSize = toEUC(c, bb)
                        if (outSize == -1) {
                            if (Character.isHighSurrogate(c)) {
                                if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                                val c2 = src.get()
                                if (!Character.isLowSurrogate(c2)) return CoderResultInternal.malformedForLength(1)
                                outSize = toEUC(c, c2, bb)
                                inSize = 2
                            } else if (Character.isLowSurrogate(c)) {
                                return CoderResultInternal.malformedForLength(1)
                            }
                        }
                    }
                    if (outSize == -1) return CoderResultInternal.unmappableForLength(inSize)
                    if (dst.remaining() < outSize) return CoderResultInternal.OVERFLOW
                    for (i in 0..<outSize) dst.put(bb[i])
                    mark += inSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            return encodeArrayLoop(src, dst)
        }

        companion object {
            fun encode(hi: Char, low: Char, bb: ByteArray): Int {
                var c: Int = Character.toCodePoint(hi, low)
                if ((c and 0xf0000) != 0x20000) return -1
                c -= 0x20000
                var index = c2bSuppIndex[c shr 8].code
                if (index == CharsetMapping.UNMAPPABLE_ENCODING) return -1
                index = index + (c and 0xff)
                val db = c2bSupp[index].code
                if (db == CharsetMapping.UNMAPPABLE_ENCODING) return -1
                val p = (c2bPlane[index].toInt() shr 4) and 0xf
                bb[0] = SS2.toByte()
                bb[1] = (0xa0 or p).toByte()
                bb[2] = (db shr 8).toByte()
                bb[3] = db.toByte()
                return 4
            }

            fun encode(c: Char, bb: ByteArray): Int {
                var index = c2bIndex[c.code shr 8].code
                if (index == CharsetMapping.UNMAPPABLE_ENCODING) return -1
                index = index + (c.code and 0xff)
                val db = c2b[index].code
                if (db == CharsetMapping.UNMAPPABLE_ENCODING) return -1
                val p = c2bPlane[index].toInt() and 0xf
                if (p == 0) {
                    bb[0] = (db shr 8).toByte()
                    bb[1] = db.toByte()
                    return 2
                } else {
                    bb[0] = SS2.toByte()
                    bb[1] = (0xa0 or p).toByte()
                    bb[2] = (db shr 8).toByte()
                    bb[3] = db.toByte()
                    return 4
                }
            }

            val c2b: CharArray
            val c2bIndex: CharArray
            val c2bSupp: CharArray
            val c2bSuppIndex: CharArray
            val c2bPlane: ByteArray

            init {
                val b1Min = Decoder.b1Min
                val b1Max = Decoder.b1Max
                val b2Min = Decoder.b2Min
                val b2Max = Decoder.b2Max
                val dbSegSize = Decoder.dbSegSize
                val b2c = Decoder.b2c
                val b2cIsSupp = Decoder.b2cIsSupp

                c2bIndex = EUC_TWMapping.c2bIndex
                c2bSuppIndex = EUC_TWMapping.c2bSuppIndex
                val c2b0 = CharArray(EUC_TWMapping.C2BSIZE)
                val c2bSupp0 = CharArray(EUC_TWMapping.C2BSUPPSIZE)
                val c2bPlane0 = ByteArray(
                    max(
                        EUC_TWMapping.C2BSIZE,
                        EUC_TWMapping.C2BSUPPSIZE
                    )
                )

                c2b0.fill(CharsetMapping.UNMAPPABLE_ENCODING_CHAR)
                c2bSupp0.fill(CharsetMapping.UNMAPPABLE_ENCODING_CHAR)

                for (p in b2c.indices) {
                    val db = b2c[p]
                    /*
                   adjust the "plane" from 0..7 to 0, 2, 3, 4, 5, 6, 7, 0xf,
                   which helps balance between footprint (to save the plane
                   info in 4 bits) and runtime performance (to require only
                   one operation "0xa0 | plane" to encode the plane byte)
                */
                    var plane = p
                    if (plane == 7) plane = 0xf
                    else if (plane != 0) plane = p + 1

                    var off = 0
                    for (b1 in b1Min..b1Max) {
                        for (b2 in b2Min..b2Max) {
                            val c: Char = db[off]
                            if (c != CharsetMapping.UNMAPPABLE_DECODING) {
                                if ((b2cIsSupp[off].toInt() and (1 shl p)) != 0) {
                                    val index = c2bSuppIndex[c.code shr 8].code + (c.code and 0xff)
                                    c2bSupp0[index] = ((b1 shl 8) + b2).toChar()
                                    c2bPlane0[index] = (c2bPlane0[index].toInt() or (plane shl 4)).toByte()
                                } else {
                                    val index = c2bIndex[c.code shr 8].code + (c.code and 0xff)
                                    c2b0[index] = ((b1 shl 8) + b2).toChar()
                                    c2bPlane0[index] = (c2bPlane0[index].toInt() or plane).toByte()
                                }
                            }
                            off++
                        }
                    }
                }
                c2b = c2b0
                c2bSupp = c2bSupp0
                c2bPlane = c2bPlane0
            }
        }
    }

    companion object {
        private const val SS2 = 0x8E
    }
}
