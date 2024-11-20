package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.euc.EUC_KR
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt


class ISO2022_KR : ISO2022("ISO-2022-KR") {
    private object Holder {
        val ksc5601_cs: Charset = EUC_KR()
    }

    override fun contains(cs: Charset): Boolean {
        // overlapping repertoire of EUC_KR, aka KSC5601
        return ((cs is EUC_KR) ||
                (cs.name() == "US-ASCII") ||
                (cs is ISO2022_KR))
    }


    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        return Encoder(this)
    }


    private class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f) {
        private var shiftout = false

        override fun implReset() {
            shiftout = false
        }

        fun decode(byte1: Byte, byte2: Byte, shiftFlag: Byte): Char {
            if (shiftFlag == SOFlag) {
                return KSC5601.decodeDouble(
                    (byte1.toInt() or MSB.toInt()) and 0xFF,
                    (byte2.toInt() or MSB.toInt()) and 0xFF
                )
            }
            return REPLACE_CHAR
        }

        fun findDesig(`in`: ByteArray, sp: Int, sl: Int): Boolean {
            if (sl - sp >= SOD.size) {
                var j = 0
                while (j < SOD.size && `in`[sp + j] == SOD[j]) {
                    j++
                }
                return j == SOD.size
            }
            return false
        }

        fun findDesigBuf(inByteBuffer: ByteBuffer): Boolean {
            if (inByteBuffer.remaining() >= SOD.size) {
                var j = 0
                inByteBuffer.mark()
                while (j < SOD.size && inByteBuffer.get() == SOD[j]) {
                    j++
                }
                if (j == SOD.size) return true
                inByteBuffer.reset()
            }
            return false
        }

        fun decodeArrayLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            var b1: Int
            var b2: Int
            var b3: Int

            try {
                while (sp < sl) {
                    b1 = sa[sp].toInt() and 0xff
                    var inputSize = 1
                    when (b1) {
                        ISO_SO -> {
                            shiftout = true
                            inputSize = 1
                        }

                        ISO_SI -> {
                            shiftout = false
                            inputSize = 1
                        }

                        ISO_ESC -> {
                            if (sl - sp - 1 < minDesignatorLength) return CoderResultInternal.UNDERFLOW

                            if (findDesig(sa, sp + 1, sl)) {
                                inputSize = SOD.size + 1
                                break
                            }
                            if (sl - sp < 2) return CoderResultInternal.UNDERFLOW
                            b1 = sa[sp + 1].toInt()
                            when (b1) {
                                ISO_SS2_7 -> {
                                    if (sl - sp < 4) return CoderResultInternal.UNDERFLOW
                                    b2 = sa[sp + 2].toInt()
                                    b3 = sa[sp + 3].toInt()
                                    if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                                    da[dp] = decode(
                                        b2.toByte(),
                                        b3.toByte(),
                                        SS2Flag
                                    )
                                    dp++
                                    inputSize = 4
                                }

                                ISO_SS3_7 -> {
                                    if (sl - sp < 4) return CoderResultInternal.UNDERFLOW
                                    b2 = sa[sp + 2].toInt()
                                    b3 = sa[sp + 3].toInt()
                                    if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                                    da[dp] = decode(
                                        b2.toByte(),
                                        b3.toByte(),
                                        SS3Flag
                                    )
                                    dp++
                                    inputSize = 4
                                }

                                else -> return CoderResultInternal.malformedForLength(2)
                            }
                        }

                        else -> {
                            if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                            if (!shiftout) {
                                da[dp++] = (sa[sp].toInt() and 0xff).toChar()
                            } else {
                                if (dl - dp < 1) return CoderResultInternal.OVERFLOW
                                if (sl - sp < 2) return CoderResultInternal.UNDERFLOW
                                b2 = sa[sp + 1].toInt() and 0xff
                                da[dp++] = decode(
                                    b1.toByte(),
                                    b2.toByte(),
                                    SOFlag
                                )
                                inputSize = 2
                            }
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

        fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()
            var b1: Int
            var b2: Int
            var b3: Int

            try {
                while (src.hasRemaining()) {
                    b1 = src.getInt()
                    var inputSize = 1
                    when (b1) {
                        ISO_SO -> shiftout = true
                        ISO_SI -> shiftout = false
                        ISO_ESC -> {
                            if (src.remaining() < minDesignatorLength) return CoderResultInternal.UNDERFLOW

                            if (findDesigBuf(src)) {
                                inputSize = SOD.size + 1
                                break
                            }

                            if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                            b1 = src.getInt()
                            when (b1) {
                                ISO_SS2_7 -> {
                                    if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                                    b2 = src.getInt()
                                    b3 = src.getInt()
                                    if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                                    dst.put(
                                        decode(
                                            b2.toByte(),
                                            b3.toByte(),
                                            SS2Flag
                                        )
                                    )
                                    inputSize = 4
                                }

                                ISO_SS3_7 -> {
                                    if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                                    b2 = src.getInt()
                                    b3 = src.getInt()
                                    if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                                    dst.put(
                                        decode(
                                            b2.toByte(),
                                            b3.toByte(),
                                            SS3Flag
                                        )
                                    )
                                    inputSize = 4
                                }

                                else -> return CoderResultInternal.malformedForLength(2)
                            }
                        }

                        else -> {
                            if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                            if (!shiftout) {
                                dst.put((b1 and 0xff).toChar())
                            } else {
                                if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                                b2 = src.getInt() and 0xff
                                dst.put(
                                    decode(
                                        b1.toByte(),
                                        b2.toByte(),
                                        SOFlag
                                    )
                                )
                                inputSize = 2
                            }
                        }
                    }
                    mark += inputSize
                }
                return CoderResultInternal.UNDERFLOW
            } catch (e: Exception) {
                e.printStackTrace()
                return CoderResultInternal.OVERFLOW
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

        companion object {
            private val SOD = byteArrayOf('$'.code.toByte(), ')'.code.toByte(), 'C'.code.toByte())

            private val KSC5601: DoubleByte.Decoder = EUC_KR().newDecoder() as DoubleByte.Decoder

            private const val ISO_ESC: Int = 0x1b
            private const val ISO_SI: Int = 0x0f
            private const val ISO_SO: Int = 0x0e
            private const val ISO_SS2_7: Int = 0x4e
            private const val ISO_SS3_7: Int = 0x4f
            private val MSB = 0x80.toByte()
            private const val REPLACE_CHAR = '\uFFFD'
            private const val minDesignatorLength: Byte = 3

            private const val SOFlag: Byte = 0
            private const val SS2Flag: Byte = 1
            private const val SS3Flag: Byte = 2
        }
    }

    private class Encoder(cs: Charset) : ISO2022.Encoder(cs) {
        init {
            SODesig = SOD
            try {
                ISOEncoder = Holder.ksc5601_cs.newEncoder()
            } catch (e: Exception) {
            }
        }

        override fun canEncode(c: Char): Boolean {
            return ISOEncoder?.canEncode(c) == true
        }

        companion object {
            private val SOD = byteArrayOf('$'.code.toByte(), ')'.code.toByte(), 'C'.code.toByte())
        }
    }
}
