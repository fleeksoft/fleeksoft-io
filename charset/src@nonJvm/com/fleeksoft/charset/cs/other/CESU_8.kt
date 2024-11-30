package com.fleeksoft.charset.cs.other

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.charset.cs.ArrayDecoder
import com.fleeksoft.charset.cs.ArrayEncoder
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.cs.Unicode
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.Buffer
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.ByteBufferFactory
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.min

/* Legal CESU-8 Byte Sequences
 *
 * #    Code Points      Bits   Bit/Byte pattern
 * 1                     7      0xxxxxxx
 *      U+0000..U+007F          00..7F
 *
 * 2                     11     110xxxxx    10xxxxxx
 *      U+0080..U+07FF          C2..DF      80..BF
 *
 * 3                     16     1110xxxx    10xxxxxx    10xxxxxx
 *      U+0800..U+0FFF          E0          A0..BF      80..BF
 *      U+1000..U+FFFF          E1..EF      80..BF      80..BF
 *
 */
internal class CESU_8 private constructor() : Unicode("CESU-8") {

    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    private class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f), ArrayDecoder {
        fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            // This method is optimized for ASCII input.
            val sa = src.array()
            val soff = src.arrayOffset()
            var sp = soff + src.position()
            val sl = soff + src.limit()

            val da = dst.array()
            val doff = dst.arrayOffset()
            var dp = doff + dst.position()
            val dl = doff + dst.limit()

            val n: Int = JLA.decodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
            sp += n
            dp += n

            while (sp < sl) {
                val b1 = sa[sp].toInt()
                if (b1 >= 0) {
                    // 1 byte, 7 bits: 0xxxxxxx
                    if (dp >= dl) return xflow(src, sp, sl, dst, dp, 1)
                    da[dp++] = b1.toChar()
                    sp++
                } else if ((b1 shr 5) == -2 && (b1 and 0x1e) != 0) {
                    // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                    if (sl - sp < 2 || dp >= dl) return xflow(src, sp, sl, dst, dp, 2)
                    val b2 = sa[sp + 1].toInt()
                    if (isNotContinuation(b2)) return malformedForLength(
                        src,
                        sp,
                        dst,
                        dp,
                        1
                    )
                    da[dp++] = (((b1 shl 6) xor b2)
                            xor
                            ((0xC0.toByte().toInt() shl 6) xor
                                    (0x80.toByte().toInt() shl 0))).toChar()
                    sp += 2
                } else if ((b1 shr 4) == -2) {
                    // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                    val srcRemaining = sl - sp
                    if (srcRemaining < 3 || dp >= dl) {
                        if (srcRemaining > 1 && isMalformed3_2(
                                b1,
                                sa[sp + 1].toInt()
                            )
                        ) return malformedForLength(src, sp, dst, dp, 1)
                        return xflow(src, sp, sl, dst, dp, 3)
                    }
                    val b2 = sa[sp + 1].toInt()
                    val b3 = sa[sp + 2].toInt()
                    if (isMalformed3(
                            b1,
                            b2,
                            b3
                        )
                    ) return malformed(src, sp, dst, dp, 3)
                    da[dp++] = ((b1 shl 12) xor
                            (b2 shl 6) xor
                            (b3 xor
                                    ((0xE0.toByte().toInt() shl 12) xor
                                            (0x80.toByte().toInt() shl 6) xor
                                            (0x80.toByte().toInt() shl 0)))).toChar()
                    sp += 3
                } else {
                    return malformed(src, sp, dst, dp, 1)
                }
            }
            return xflow(src, sp, sl, dst, dp, 0)
        }

        fun decodeBufferLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            var mark = src.position()
            val limit = src.limit()
            while (mark < limit) {
                val b1 = src.getInt()
                if (b1 >= 0) {
                    // 1 byte, 7 bits: 0xxxxxxx
                    if (dst.remaining() < 1) return xflow(src, mark, 1) // overflow

                    dst.put(b1.toChar())
                    mark++
                } else if ((b1 shr 5) == -2 && (b1 and 0x1e) != 0) {
                    // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                    if (limit - mark < 2 || dst.remaining() < 1) return xflow(src, mark, 2)
                    val b2 = src.getInt()
                    if (isNotContinuation(b2)) return malformedForLength(
                        src,
                        mark,
                        1
                    )
                    dst.put(
                        (((b1 shl 6) xor b2)
                                xor
                                ((0xC0.toByte().toInt() shl 6) xor
                                        (0x80.toByte().toInt() shl 0))).toChar()
                    )
                    mark += 2
                } else if ((b1 shr 4) == -2) {
                    // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                    val srcRemaining = limit - mark
                    if (srcRemaining < 3 || dst.remaining() < 1) {
                        if (srcRemaining > 1 && isMalformed3_2(
                                b1,
                                src.getInt()
                            )
                        ) return malformedForLength(src, mark, 1)
                        return xflow(src, mark, 3)
                    }
                    val b2 = src.getInt()
                    val b3 = src.getInt()
                    if (isMalformed3(
                            b1,
                            b2,
                            b3
                        )
                    ) return malformed(src, mark, 3)
                    dst.put(
                        ((b1 shl 12) xor
                                (b2 shl 6) xor
                                (b3 xor
                                        ((0xE0.toByte().toInt() shl 12) xor
                                                (0x80.toByte().toInt() shl 6) xor
                                                (0x80.toByte().toInt() shl 0)))).toChar()
                    )
                    mark += 3
                } else {
                    return malformed(src, mark, 1)
                }
            }
            return xflow(src, mark, 0)
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                decodeArrayLoop(src, dst)
            } else {
                decodeBufferLoop(src, dst)
            }
        }

        // returns -1 if there is/are malformed byte(s) and the
        // "action" for malformed input is not REPLACE.
        override fun decode(sa: ByteArray, sp: Int, len: Int, da: CharArray): Int {
            var sp = sp
            val sl = sp + len
            var dp = 0
            val dlASCII: Int = min(len, da.size)
            var bb: ByteBuffer? = null // only necessary if malformed

            // ASCII only optimized loop
            while (dp < dlASCII && sa[sp] >= 0) da[dp++] = Char(sa[sp++].toUShort())

            while (sp < sl) {
                val b1 = sa[sp++].toInt()
                if (b1 >= 0) {
                    // 1 byte, 7 bits: 0xxxxxxx
                    da[dp++] = b1.toChar()
                } else if ((b1 shr 5) == -2 && (b1 and 0x1e) != 0) {
                    // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                    if (sp < sl) {
                        val b2 = sa[sp++].toInt()
                        if (isNotContinuation(b2)) {
                            if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                            da[dp++] = replacement()[0]
                            sp-- // malformedN(bb, 2) always returns 1
                        } else {
                            da[dp++] = (((b1 shl 6) xor b2) xor
                                    ((0xC0.toByte().toInt() shl 6) xor
                                            (0x80.toByte().toInt() shl 0))).toChar()
                        }
                        continue
                    }
                    if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                    da[dp++] = replacement()[0]
                    return dp
                } else if ((b1 shr 4) == -2) {
                    // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                    if (sp + 1 < sl) {
                        val b2 = sa[sp++].toInt()
                        val b3 = sa[sp++].toInt()
                        if (isMalformed3(b1, b2, b3)) {
                            if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                            da[dp++] = replacement()[0]
                            sp -= 3
                            bb = getByteBuffer(bb, sa, sp)
                            sp += malformedN(bb, 3).length()
                        } else {
                            da[dp++] = ((b1 shl 12) xor
                                    (b2 shl 6) xor
                                    (b3 xor
                                            ((0xE0.toByte().toInt() shl 12) xor
                                                    (0x80.toByte().toInt() shl 6) xor
                                                    (0x80.toByte().toInt() shl 0)))).toChar()
                        }
                        continue
                    }
                    if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                    if (sp < sl && isMalformed3_2(b1, sa[sp].toInt())) {
                        da[dp++] = replacement()[0]
                        continue
                    }
                    da[dp++] = replacement()[0]
                    return dp
                } else {
                    if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                    da[dp++] = replacement()[0]
                }
            }
            return dp
        }

        companion object {
            private fun isNotContinuation(b: Int): Boolean {
                return (b and 0xc0) != 0x80
            }

            //  [E0]     [A0..BF] [80..BF]
            //  [E1..EF] [80..BF] [80..BF]
            private fun isMalformed3(b1: Int, b2: Int, b3: Int): Boolean {
                return (b1 == 0xe0.toByte().toInt() && (b2 and 0xe0) == 0x80) || (b2 and 0xc0) != 0x80 || (b3 and 0xc0) != 0x80
            }

            // only used when there is only one byte left in src buffer
            private fun isMalformed3_2(b1: Int, b2: Int): Boolean {
                return (b1 == 0xe0.toByte().toInt() && (b2 and 0xe0) == 0x80) ||
                        (b2 and 0xc0) != 0x80
            }

            private fun malformedN(src: ByteBuffer, nb: Int): CoderResult {
                when (nb) {
                    1, 2 -> return CoderResultInternal.malformedForLength(1)
                    3 -> {
                        val b1 = src.get()
                        val b2 = src.getInt() // no need to lookup b3
                        return CoderResultInternal.malformedForLength(
                            if ((b1 == 0xe0.toByte() && (b2 and 0xe0) == 0x80) ||
                                isNotContinuation(b2)
                            ) 1 else 2
                        )
                    }

                    4 -> {
                        val b1 = src.getInt() and 0xff
                        val b2 = src.getInt() and 0xff
                        if (b1 > 0xf4 ||
                            (b1 == 0xf0 && (b2 < 0x90 || b2 > 0xbf)) ||
                            (b1 == 0xf4 && (b2 and 0xf0) != 0x80) ||
                            isNotContinuation(b2)
                        ) return CoderResultInternal.malformedForLength(1)
                        if (isNotContinuation(src.getInt())) return CoderResultInternal.malformedForLength(2)
                        return CoderResultInternal.malformedForLength(3)
                    }

                    else -> {
                        throw Exception("This is unexpected state")
                    }
                }
            }

            private fun malformed(
                src: ByteBuffer, sp: Int,
                dst: CharBuffer, dp: Int,
                nb: Int
            ): CoderResult {
                src.position(sp - src.arrayOffset())
                val cr: CoderResult = malformedN(src, nb)
                updatePositions(src, sp, dst, dp)
                return cr
            }


            private fun malformed(
                src: ByteBuffer,
                mark: Int, nb: Int
            ): CoderResult {
                src.position(mark)
                val cr: CoderResult = malformedN(src, nb)
                src.position(mark)
                return cr
            }

            private fun malformedForLength(
                src: ByteBuffer,
                sp: Int,
                dst: CharBuffer,
                dp: Int,
                malformedNB: Int
            ): CoderResult {
                updatePositions(src, sp, dst, dp)
                return CoderResultInternal.malformedForLength(malformedNB)
            }

            private fun malformedForLength(
                src: ByteBuffer,
                mark: Int,
                malformedNB: Int
            ): CoderResult {
                src.position(mark)
                return CoderResultInternal.malformedForLength(malformedNB)
            }


            private fun xflow(
                src: Buffer, sp: Int, sl: Int,
                dst: Buffer, dp: Int, nb: Int
            ): CoderResult {
                updatePositions(src, sp, dst, dp)
                return if (nb == 0 || sl - sp < nb) CoderResultInternal.UNDERFLOW else CoderResultInternal.OVERFLOW
            }

            private fun xflow(src: Buffer, mark: Int, nb: Int): CoderResult {
                src.position(mark)
                return if (nb == 0 || src.remaining() < nb) CoderResultInternal.UNDERFLOW else CoderResultInternal.OVERFLOW
            }

            private fun getByteBuffer(bb: ByteBuffer?, ba: ByteArray?, sp: Int): ByteBuffer {
                var bb = bb
                if (bb == null) bb = ByteBufferFactory.wrap(ba!!)
                bb.position(sp)
                return bb
            }
        }
    }

    private class Encoder(cs: Charset) : CharsetEncoder(cs, 1.1f, 3.0f), ArrayEncoder {
        override fun canEncode(c: Char): Boolean {
            return !Character.isSurrogate(c)
        }

        override fun isLegalReplacement(repl: ByteArray): Boolean {
            return ((repl.size == 1 && repl[0] >= 0) ||
                    super.isLegalReplacement(repl))
        }

        private var sgp: Surrogate.Parser? = null
        fun encodeArrayLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            // Handle ASCII-only prefix
            val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
            sp += n
            dp += n

            while (sp < sl) {
                val c = sa[sp]
                if (c.code < 0x80) {
                    // Have at most seven bits
                    if (dp >= dl) return overflow(src, sp, dst, dp)
                    da[dp++] = c.code.toByte()
                } else if (c.code < 0x800) {
                    // 2 bytes, 11 bits
                    if (dl - dp < 2) return overflow(src, sp, dst, dp)
                    da[dp++] = (0xc0 or (c.code shr 6)).toByte()
                    da[dp++] = (0x80 or (c.code and 0x3f)).toByte()
                } else if (Character.isSurrogate(c)) {
                    // Have a surrogate pair
                    if (sgp == null) sgp = Surrogate.Parser()
                    val uc = sgp!!.parse(c, sa, sp, sl)
                    if (uc < 0) {
                        updatePositions(src, sp, dst, dp)
                        return sgp!!.error()
                    }
                    if (dl - dp < 6) return overflow(src, sp, dst, dp)
                    to3Bytes(da, dp, Character.highSurrogate(uc))
                    dp += 3
                    to3Bytes(da, dp, Character.lowSurrogate(uc))
                    dp += 3
                    sp++ // 2 chars
                } else {
                    // 3 bytes, 16 bits
                    if (dl - dp < 3) return overflow(src, sp, dst, dp)
                    to3Bytes(da, dp, c)
                    dp += 3
                }
                sp++
            }
            updatePositions(src, sp, dst, dp)
            return CoderResultInternal.UNDERFLOW
        }

        fun encodeBufferLoop(
            src: CharBuffer,
            dst: ByteBuffer
        ): CoderResult {
            var mark = src.position()
            while (src.hasRemaining()) {
                val c = src.get()
                if (c.code < 0x80) {
                    // Have at most seven bits
                    if (!dst.hasRemaining()) return overflow(src, mark)
                    dst.put(c.code.toByte())
                } else if (c.code < 0x800) {
                    // 2 bytes, 11 bits
                    if (dst.remaining() < 2) return overflow(src, mark)
                    dst.put((0xc0 or (c.code shr 6)).toByte())
                    dst.put((0x80 or (c.code and 0x3f)).toByte())
                } else if (Character.isSurrogate(c)) {
                    // Have a surrogate pair
                    if (sgp == null) sgp = Surrogate.Parser()
                    val uc = sgp!!.parse(c, src)
                    if (uc < 0) {
                        src.position(mark)
                        return sgp!!.error()
                    }
                    if (dst.remaining() < 6) return overflow(src, mark)
                    to3Bytes(dst, Character.highSurrogate(uc))
                    to3Bytes(dst, Character.lowSurrogate(uc))
                    mark++ // 2 chars
                } else {
                    // 3 bytes, 16 bits
                    if (dst.remaining() < 3) return overflow(src, mark)
                    to3Bytes(dst, c)
                }
                mark++
            }
            src.position(mark)
            return CoderResultInternal.UNDERFLOW
        }

        override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            return if (src.hasArray() && dst.hasArray()) {
                encodeArrayLoop(src, dst)
            } else {
                encodeBufferLoop(src, dst)
            }
        }

        // returns -1 if there is malformed char(s) and the
        // "action" for malformed input is not REPLACE.
        override fun encode(sa: CharArray, sp: Int, len: Int, da: ByteArray): Int {
            var sp = sp
            val sl = sp + len
            var dp = 0

            // Handle ASCII-only prefix
            val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(len, da.size))
            sp += n
            dp += n

            while (sp < sl) {
                val c = sa[sp++]
                if (c.code < 0x80) {
                    // Have at most seven bits
                    da[dp++] = c.code.toByte()
                } else if (c.code < 0x800) {
                    // 2 bytes, 11 bits
                    da[dp++] = (0xc0 or (c.code shr 6)).toByte()
                    da[dp++] = (0x80 or (c.code and 0x3f)).toByte()
                } else if (Character.isSurrogate(c)) {
                    if (sgp == null) sgp = Surrogate.Parser()
                    val uc = sgp!!.parse(c, sa, sp - 1, sl)
                    if (uc < 0) {
                        if (malformedInputAction() != CodingErrorActionValue.REPLACE) return -1
                        da[dp++] = replacement()[0]
                    } else {
                        to3Bytes(da, dp, Character.highSurrogate(uc))
                        dp += 3
                        to3Bytes(da, dp, Character.lowSurrogate(uc))
                        dp += 3
                        sp++ // 2 chars
                    }
                } else {
                    // 3 bytes, 16 bits
                    to3Bytes(da, dp, c)
                    dp += 3
                }
            }
            return dp
        }

        companion object {
            private fun overflow(
                src: CharBuffer, sp: Int,
                dst: ByteBuffer, dp: Int
            ): CoderResult {
                updatePositions(src, sp, dst, dp)
                return CoderResultInternal.OVERFLOW
            }

            private fun overflow(src: CharBuffer, mark: Int): CoderResult {
                src.position(mark)
                return CoderResultInternal.OVERFLOW
            }

            private fun to3Bytes(da: ByteArray, dp: Int, c: Char) {
                da[dp] = (0xe0 or ((c.code shr 12))).toByte()
                da[dp + 1] = (0x80 or ((c.code shr 6) and 0x3f)).toByte()
                da[dp + 2] = (0x80 or (c.code and 0x3f)).toByte()
            }

            private fun to3Bytes(dst: ByteBuffer, c: Char) {
                dst.put((0xe0 or ((c.code shr 12))).toByte())
                dst.put((0x80 or ((c.code shr 6) and 0x3f)).toByte())
                dst.put((0x80 or (c.code and 0x3f)).toByte())
            }
        }
    }

    companion object {
        val INSTANCE = CESU_8()

        private fun updatePositions(src: Buffer, sp: Int, dst: Buffer, dp: Int) {
            src.position(sp - src.arrayOffset())
            dst.position(dp - dst.arrayOffset())
        }
    }
}
