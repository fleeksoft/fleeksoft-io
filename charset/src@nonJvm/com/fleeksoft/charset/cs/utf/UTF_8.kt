package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.cs.Unicode
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.JLA
import com.fleeksoft.io.Buffer
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character
import kotlin.math.min

class UTF_8 : Unicode("UTF-8") {
    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }

    companion object {
        val INSTANCE = UTF_8()

        private fun updatePositions(src: Buffer, sp: Int, dst: Buffer, dp: Int) {
            src.position(sp - src.arrayOffset())
            dst.position(dp - dst.arrayOffset())
        }

        private class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f) {
            fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
                // This method is optimized for ASCII input.
                val sa: ByteArray = src.array()
                val soff: Int = src.arrayOffset()
                var sp: Int = soff + src.position()
                val sl: Int = soff + src.limit()

                val da: CharArray = dst.array()
                val doff: Int = dst.arrayOffset()
                var dp: Int = doff + dst.position()
                val dl: Int = doff + dst.limit()

                val n: Int = JLA.decodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
                sp += n
                dp += n

                while (sp < sl) {
                    var b1 = sa[sp].toInt()
                    if (b1 >= 0) {
                        // 1 byte, 7 bits: 0xxxxxxx
                        if (dp >= dl) return xflow(src, sp, sl, dst, dp, 1)
                        da[dp++] = b1.toChar()
                        sp++
                    } else if ((b1 shr 5) == -2 && (b1 and 0x1e) != 0) {
                        // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                        //                   [C2..DF] [80..BF]
                        if (sl - sp < 2 || dp >= dl) return xflow(src, sp, sl, dst, dp, 2)
                        val b2 = sa[sp + 1].toInt()
                        // Now we check the first byte of 2-byte sequence as
                        //     if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0)
                        // no longer need to check b1 against c1 & c0 for
                        // malformed as we did in previous version
                        //   (b1 & 0x1e) == 0x0 || (b2 & 0xc0) != 0x80;
                        // only need to check the second byte b2.
                        if (isNotContinuation(b2)) return malformedForLength(src, sp, dst, dp, 1)
                        da[dp++] = (((b1 shl 6) xor b2)
                                xor
                                ((0xC0.toByte().toInt() shl 6) xor
                                        (0x80.toByte().toInt() shl 0))).toChar()
                        sp += 2
                    } else if ((b1 shr 4) == -2) {
                        // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                        val srcRemaining = sl - sp
                        if (srcRemaining < 3 || dp >= dl) {
                            if (srcRemaining > 1 && isMalformed3_2(b1, sa[sp + 1].toInt())) return malformedForLength(src, sp, dst, dp, 1)
                            return xflow(src, sp, sl, dst, dp, 3)
                        }
                        val b2 = sa[sp + 1].toInt()
                        val b3 = sa[sp + 2].toInt()
                        if (isMalformed3(b1, b2, b3)) return malformed(src, sp, dst, dp, 3)!!
                        val c = ((b1 shl 12) xor
                                (b2 shl 6) xor
                                (b3 xor
                                        ((0xE0.toByte().toInt() shl 12) xor
                                                (0x80.toByte().toInt() shl 6) xor
                                                (0x80.toByte().toInt() shl 0)))).toChar()
                        if (c.isSurrogate()) return malformedForLength(src, sp, dst, dp, 3)
                        da[dp++] = c
                        sp += 3
                    } else if ((b1 shr 3) == -2) {
                        // 4 bytes, 21 bits: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                        val srcRemaining = sl - sp
                        if (srcRemaining < 4 || dl - dp < 2) {
                            b1 = b1 and 0xff
                            if (b1 > 0xf4 ||
                                srcRemaining > 1 && isMalformed4_2(b1, sa[sp + 1].toInt() and 0xff)
                            ) return malformedForLength(src, sp, dst, dp, 1)
                            if (srcRemaining > 2 && isMalformed4_3(sa[sp + 2].toInt())) return malformedForLength(src, sp, dst, dp, 2)
                            return xflow(src, sp, sl, dst, dp, 4)
                        }
                        val b2 = sa[sp + 1].toInt()
                        val b3 = sa[sp + 2].toInt()
                        val b4 = sa[sp + 3].toInt()
                        val uc: Int = ((b1 shl 18) xor
                                (b2 shl 12) xor
                                (b3 shl 6) xor
                                (b4 xor
                                        ((0xF0.toByte().toInt() shl 18) xor
                                                (0x80.toByte().toInt() shl 12) xor
                                                (0x80.toByte().toInt() shl 6) xor
                                                (0x80.toByte().toInt() shl 0))))
                        if (isMalformed4(b2, b3, b4) ||  // shortest form check
                            !Character.isSupplementaryCodePoint(uc)
                        ) {
                            return malformed(src, sp, dst, dp, 4)!!
                        }
                        da[dp++] = Character.highSurrogate(uc)
                        da[dp++] = Character.lowSurrogate(uc)
                        sp += 4
                    } else return malformed(src, sp, dst, dp, 1)!!
                }
                return xflow(src, sp, sl, dst, dp, 0)
            }

            override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
                return decodeArrayLoop(src, dst)
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

                //  [F0]     [90..BF] [80..BF] [80..BF]
                //  [F1..F3] [80..BF] [80..BF] [80..BF]
                //  [F4]     [80..8F] [80..BF] [80..BF]
                //  only check 80-be range here, the [0xf0,0x80...] and [0xf4,0x90-...]
                //  will be checked by Character.isSupplementaryCodePoint(uc)
                private fun isMalformed4(b2: Int, b3: Int, b4: Int): Boolean {
                    return (b2 and 0xc0) != 0x80 || (b3 and 0xc0) != 0x80 || (b4 and 0xc0) != 0x80
                }

                // only used when there is less than 4 bytes left in src buffer.
                // both b1 and b2 should be "& 0xff" before passed in.
                private fun isMalformed4_2(b1: Int, b2: Int): Boolean {
                    return (b1 == 0xf0 && (b2 < 0x90 || b2 > 0xbf)) ||
                            (b1 == 0xf4 && (b2 and 0xf0) != 0x80) || (b2 and 0xc0) != 0x80
                }

                // tests if b1 and b2 are malformed as the first 2 bytes of a
                // legal`4-byte utf-8 byte sequence.
                // only used when there is less than 4 bytes left in src buffer,
                // after isMalformed4_2 has been invoked.
                private fun isMalformed4_3(b3: Int): Boolean {
                    return (b3 and 0xc0) != 0x80
                }

                private fun malformedN(src: ByteBuffer, nb: Int): CoderResult {
                    when (nb) {
                        1, 2 -> return CoderResultInternal.malformedForLength(1)
                        3 -> {
                            val b1: Byte = src.get()
                            val b2: Int = src.getInt() // no need to lookup b3
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
                            require(false)
                            throw Exception("Malformed for boundary code: $nb")
                        }
                    }
                }

                private fun malformed(
                    src: ByteBuffer, sp: Int,
                    dst: CharBuffer, dp: Int,
                    nb: Int
                ): CoderResult {
                    src.position(sp - src.arrayOffset())
                    val cr = malformedN(src, nb)
                    updatePositions(src, sp, dst, dp)
                    return cr
                }


                private fun malformed(
                    src: ByteBuffer,
                    mark: Int, nb: Int
                ): CoderResult {
                    src.position(mark)
                    val cr = malformedN(src, nb)
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
            }
        }

        private class Encoder(cs: Charset) : CharsetEncoder(cs, 1.1f, 3.0f) {
            override fun canEncode(c: Char): Boolean {
                return !Character.isSurrogate(c)
            }

            override fun isLegalReplacement(repl: ByteArray): Boolean {
                return ((repl.size == 1 && repl[0] >= 0) ||
                        super.isLegalReplacement(repl))
            }

            private var sgp: Surrogate.Parser? = null
            private fun encodeArrayLoop(
                src: CharBuffer,
                dst: ByteBuffer
            ): CoderResult {
                val sa: CharArray = src.array()
                var sp: Int = src.arrayOffset() + src.position()
                val sl: Int = src.arrayOffset() + src.limit()

                val da: ByteArray = dst.array()
                var dp: Int = dst.arrayOffset() + dst.position()
                val dl: Int = dst.arrayOffset() + dst.limit()

                // Handle ASCII-only prefix
                val n: Int = JLA.encodeASCII(sa, sp, da, dp, min(sl - sp, dl - dp))
                sp += n
                dp += n

                if (sp < sl) {
                    return encodeArrayLoopSlow(src, sa, sp, sl, dst, da, dp, dl)
                } else {
                    updatePositions(src, sp, dst, dp)
                    return CoderResultInternal.UNDERFLOW
                }
            }

            private fun encodeArrayLoopSlow(
                src: CharBuffer, sa: CharArray, sp: Int, sl: Int,
                dst: ByteBuffer, da: ByteArray, dp: Int, dl: Int
            ): CoderResult {
                var sp = sp
                var dp = dp
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
                        if (dl - dp < 4) return overflow(src, sp, dst, dp)
                        da[dp++] = (0xf0 or ((uc shr 18))).toByte()
                        da[dp++] = (0x80 or ((uc shr 12) and 0x3f)).toByte()
                        da[dp++] = (0x80 or ((uc shr 6) and 0x3f)).toByte()
                        da[dp++] = (0x80 or (uc and 0x3f)).toByte()
                        sp++ // 2 chars
                    } else {
                        // 3 bytes, 16 bits
                        if (dl - dp < 3) return overflow(src, sp, dst, dp)
                        da[dp++] = (0xe0 or ((c.code shr 12))).toByte()
                        da[dp++] = (0x80 or ((c.code shr 6) and 0x3f)).toByte()
                        da[dp++] = (0x80 or (c.code and 0x3f)).toByte()
                    }
                    sp++
                }
                updatePositions(src, sp, dst, dp)
                return CoderResultInternal.UNDERFLOW
            }

            private fun encodeBufferLoop(
                src: CharBuffer,
                dst: ByteBuffer
            ): CoderResult {
                var mark: Int = src.position()
                while (src.hasRemaining()) {
                    val c: Char = src.get()
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
                        if (dst.remaining() < 4) return overflow(src, mark)
                        dst.put((0xf0 or ((uc shr 18))).toByte())
                        dst.put((0x80 or ((uc shr 12) and 0x3f)).toByte())
                        dst.put((0x80 or ((uc shr 6) and 0x3f)).toByte())
                        dst.put((0x80 or (uc and 0x3f)).toByte())
                        mark++ // 2 chars
                    } else {
                        // 3 bytes, 16 bits
                        if (dst.remaining() < 3) return overflow(src, mark)
                        dst.put((0xe0 or ((c.code shr 12))).toByte())
                        dst.put((0x80 or ((c.code shr 6) and 0x3f)).toByte())
                        dst.put((0x80 or (c.code and 0x3f)).toByte())
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
            }
        }
    }
}