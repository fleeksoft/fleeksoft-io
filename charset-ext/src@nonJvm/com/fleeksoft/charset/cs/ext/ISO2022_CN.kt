package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.DoubleByte
import com.fleeksoft.charset.cs.US_ASCII
import com.fleeksoft.charset.cs.euc.EUC_CN
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import kotlin.experimental.or

class ISO2022_CN : Charset("ISO-2022-CN", null) {

    override fun contains(cs: Charset): Boolean {
        return ((cs is EUC_CN) // GB2312-80 repertoire
                || (cs is US_ASCII)
                || (cs is EUC_TW) // CNS11643 repertoire
                || (cs is ISO2022_CN))
    }


    override fun newDecoder(): CharsetDecoder {
        return Decoder(this)
    }

    override fun newEncoder(): CharsetEncoder {
        throw UnsupportedOperationException("Encoder not supported for this charset")
    }

    override fun canEncode(): Boolean {
        return false
    }

    internal class Decoder(cs: Charset) : CharsetDecoder(cs, 1.0f, 1.0f) {
        private var shiftOut = false
        private var currentSODesig: Byte

        init {
            currentSODesig = SODesigGB
        }

        override fun implReset() {
            shiftOut = false
            currentSODesig = SODesigGB
        }

        private fun cnsDecode(byte1: Byte, byte2: Byte, SS: Byte): Char {
            var byte1 = byte1
            var byte2 = byte2
            byte1 = byte1 or MSB
            byte2 = byte2 or MSB
            val p: Int
            if (SS == ISO_SS2_7) p = 1 //plane 2, index -- 1
            else if (SS == ISO_SS3_7) p = 2 //plane 3, index -- 2
            else return REPLACE_CHAR //never happen.

            return EUC_TW.Decoder.decodeSingleOrReplace(
                byte1.toInt() and 0xff,
                byte2.toInt() and 0xff,
                p,
                REPLACE_CHAR
            )
        }

        private fun SODecode(byte1: Byte, byte2: Byte, SOD: Byte): Char {
            var byte1 = byte1
            var byte2 = byte2
            byte1 = byte1 or MSB
            byte2 = byte2 or MSB
            if (SOD == SODesigGB) {
                return GB2312.decodeDouble(
                    byte1.toInt() and 0xff,
                    byte2.toInt() and 0xff
                )
            } else {    // SOD == SODesigCNS
                return EUC_TW.Decoder.decodeSingleOrReplace(
                    byte1.toInt() and 0xff,
                    byte2.toInt() and 0xff,
                    0,
                    REPLACE_CHAR
                )
            }
        }

        private fun decodeBufferLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            var mark = src.position()
            var b1: Byte
            var b2: Byte
            var b3: Byte
            var b4: Byte
            var inputSize: Int
            var c: Char
            try {
                while (src.hasRemaining()) {
                    b1 = src.get()
                    inputSize = 1

                    while (b1 == ISO_ESC || b1 == ISO_SO || b1 == ISO_SI) {
                        if (b1 == ISO_ESC) {  // ESC
                            currentSODesig = SODesigGB

                            if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW

                            b2 = src.get()
                            inputSize++

                            if ((b2.toInt() and 0x80.toByte().toInt()) != 0) return CoderResultInternal.malformedForLength(inputSize)

                            if (b2 == 0x24.toByte()) {
                                if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW

                                b3 = src.get()
                                inputSize++

                                if ((b3.toInt() and 0x80.toByte().toInt()) != 0) return CoderResultInternal.malformedForLength(inputSize)
                                if (b3 == 'A'.code.toByte()) {              // "$A"
                                    currentSODesig = SODesigGB
                                } else if (b3 == ')'.code.toByte()) {
                                    if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                                    b4 = src.get()
                                    inputSize++
                                    if (b4 == 'A'.code.toByte()) {          // "$)A"
                                        currentSODesig = SODesigGB
                                    } else if (b4 == 'G'.code.toByte()) {   // "$)G"
                                        currentSODesig = SODesigCNS
                                    } else {
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else if (b3 == '*'.code.toByte()) {
                                    if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                                    b4 = src.get()
                                    inputSize++
                                    if (b4 != 'H'.code.toByte()) {         // "$*H"
                                        //SS2Desig -> CNS-P1
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else if (b3 == '+'.code.toByte()) {
                                    if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                                    b4 = src.get()
                                    inputSize++
                                    if (b4 != 'I'.code.toByte()) {          // "$+I"
                                        //SS3Desig -> CNS-P2.
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else if (b2 == ISO_SS2_7 || b2 == ISO_SS3_7) {
                                if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                                b3 = src.get()
                                b4 = src.get()
                                inputSize += 2
                                if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW
                                //SS2->CNS-P2, SS3->CNS-P3
                                c = cnsDecode(b3, b4, b2)
                                if (c == REPLACE_CHAR) return CoderResultInternal.unmappableForLength(
                                    inputSize
                                )
                                dst.put(c)
                            } else {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                        } else if (b1 == ISO_SO) {
                            shiftOut = true
                        } else if (b1 == ISO_SI) { // shift back in
                            shiftOut = false
                        }
                        mark += inputSize
                        if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                        b1 = src.get()
                        inputSize = 1
                    }

                    if (dst.remaining() < 1) return CoderResultInternal.OVERFLOW

                    if (!shiftOut) {
                        dst.put((b1.toInt() and 0xff).toChar()) //clear the upper byte
                        mark += inputSize
                    } else {
                        if (src.remaining() < 1) return CoderResultInternal.UNDERFLOW
                        b2 = src.get()
                        inputSize++
                        c = SODecode(b1, b2, currentSODesig)
                        if (c == REPLACE_CHAR) return CoderResultInternal.unmappableForLength(inputSize)
                        dst.put(c)
                        mark += inputSize
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        private fun decodeArrayLoop(
            src: ByteBuffer,
            dst: CharBuffer
        ): CoderResult {
            var inputSize: Int
            var b1: Byte
            var b2: Byte
            var b3: Byte
            var b4: Byte
            var c: Char

            val sa = src.array()
            var sp = src.arrayOffset() + src.position()
            val sl = src.arrayOffset() + src.limit()

            val da = dst.array()
            var dp = dst.arrayOffset() + dst.position()
            val dl = dst.arrayOffset() + dst.limit()

            try {
                while (sp < sl) {
                    b1 = sa[sp]
                    inputSize = 1

                    while (b1 == ISO_ESC || b1 == ISO_SO || b1 == ISO_SI) {
                        if (b1 == ISO_ESC) {  // ESC
                            currentSODesig = SODesigGB

                            if (sp + 2 > sl) return CoderResultInternal.UNDERFLOW

                            b2 = sa[sp + 1]
                            inputSize++

                            if ((b2.toInt() and 0x80.toByte().toInt()) != 0) return CoderResultInternal.malformedForLength(inputSize)
                            if (b2 == 0x24.toByte()) {
                                if (sp + 3 > sl) return CoderResultInternal.UNDERFLOW

                                b3 = sa[sp + 2]
                                inputSize++

                                if ((b3.toInt() and 0x80.toByte().toInt()) != 0) return CoderResultInternal.malformedForLength(inputSize)
                                if (b3 == 'A'.code.toByte()) {              // "$A"
                                    /* <ESC>$A is not a legal designator sequence for
                                       ISO2022_CN, it is listed as an escape sequence
                                       for GB2312 in ISO2022-JP-2. Keep it here just for
                                       the sake of "compatibility".
                                     */
                                    currentSODesig = SODesigGB
                                } else if (b3 == ')'.code.toByte()) {
                                    if (sp + 4 > sl) return CoderResultInternal.UNDERFLOW
                                    b4 = sa[sp + 3]
                                    inputSize++

                                    if (b4 == 'A'.code.toByte()) {          // "$)A"
                                        currentSODesig = SODesigGB
                                    } else if (b4 == 'G'.code.toByte()) {   // "$)G"
                                        currentSODesig = SODesigCNS
                                    } else {
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else if (b3 == '*'.code.toByte()) {
                                    if (sp + 4 > sl) return CoderResultInternal.UNDERFLOW
                                    b4 = sa[sp + 3]
                                    inputSize++
                                    if (b4 != 'H'.code.toByte()) {          // "$*H"
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else if (b3 == '+'.code.toByte()) {
                                    if (sp + 4 > sl) return CoderResultInternal.UNDERFLOW
                                    b4 = sa[sp + 3]
                                    inputSize++
                                    if (b4 != 'I'.code.toByte()) {          // "$+I"
                                        return CoderResultInternal.malformedForLength(inputSize)
                                    }
                                } else {
                                    return CoderResultInternal.malformedForLength(inputSize)
                                }
                            } else if (b2 == ISO_SS2_7 || b2 == ISO_SS3_7) {
                                if (sp + 4 > sl) {
                                    return CoderResultInternal.UNDERFLOW
                                }
                                b3 = sa[sp + 2]
                                b4 = sa[sp + 3]
                                if (dl - dp < 1) {
                                    return CoderResultInternal.OVERFLOW
                                }
                                inputSize += 2
                                c = cnsDecode(b3, b4, b2)
                                if (c == REPLACE_CHAR) return CoderResultInternal.unmappableForLength(
                                    inputSize
                                )
                                da[dp++] = c
                            } else {
                                return CoderResultInternal.malformedForLength(inputSize)
                            }
                        } else if (b1 == ISO_SO) {
                            shiftOut = true
                        } else if (b1 == ISO_SI) { // shift back in
                            shiftOut = false
                        }
                        sp += inputSize
                        if (sp + 1 > sl) return CoderResultInternal.UNDERFLOW
                        b1 = sa[sp]
                        inputSize = 1
                    }

                    if (dl - dp < 1) {
                        return CoderResultInternal.OVERFLOW
                    }

                    if (!shiftOut) {
                        da[dp++] = (b1.toInt() and 0xff).toChar() //clear the upper byte
                    } else {
                        if (sp + 2 > sl) return CoderResultInternal.UNDERFLOW
                        b2 = sa[sp + 1]
                        inputSize++
                        c = SODecode(b1, b2, currentSODesig)
                        if (c == REPLACE_CHAR) return CoderResultInternal.unmappableForLength(inputSize)
                        da[dp++] = c
                    }
                    sp += inputSize
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
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
            private val GB2312 = EUC_CN().newDecoder() as DoubleByte.Decoder
        }
    }

    companion object {
        private const val ISO_ESC: Byte = 0x1b
        private const val ISO_SI: Byte = 0x0f
        private const val ISO_SO: Byte = 0x0e
        private const val ISO_SS2_7: Byte = 0x4e
        private const val ISO_SS3_7: Byte = 0x4f
        private val MSB = 0x80.toByte()
        private const val REPLACE_CHAR = '\uFFFD'

        private const val SODesigGB: Byte = 0
        private const val SODesigCNS: Byte = 1
    }
}
