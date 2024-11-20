package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.ByteBufferFactory
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory
import com.fleeksoft.lang.Character

abstract class ISO2022(name: String) : Charset(name, null) {
    abstract override fun newDecoder(): CharsetDecoder

    abstract override fun newEncoder(): CharsetEncoder

    // No default Decoder implementation is provided here; the concrete
    // encodings differ enough that most had been specialized for
    // performance reasons, leaving the generic implementation that existed
    // here before JDK-8261418 unused except by ISO2022_KR. As both a
    // simplification and an optimization the implementation was moved
    // there and specialized.
    protected open class Encoder(cs: Charset) : CharsetEncoder(cs, 4.0f, 8.0f) {
        private val sgp: Surrogate.Parser = Surrogate.Parser()
        val maximumDesignatorLength: Byte = 4

        lateinit var SODesig: ByteArray
        var SS2Desig: ByteArray? = null
        var SS3Desig: ByteArray? = null

        var ISOEncoder: CharsetEncoder? = null

        private var shiftout = false
        private var SODesDefined = false
        private var SS2DesDefined = false
        private var SS3DesDefined = false

        private var newshiftout = false
        private var newSODesDefined = false
        private var newSS2DesDefined = false
        private var newSS3DesDefined = false

        override fun canEncode(c: Char): Boolean {
            return (ISOEncoder!!.canEncode(c))
        }

        override fun implReset() {
            shiftout = false
            SODesDefined = false
            SS2DesDefined = false
            SS3DesDefined = false
        }

        private fun unicodeToNative(unicode: Char, ebyte: ByteArray?): Int {
            var index = 0
            val convChar = charArrayOf(unicode)
            val convByte = ByteArray(4)
            val converted: Int

            try {
                val cc = CharBufferFactory.wrap(convChar)
                val bb = ByteBufferFactory.wrap(convByte)
                ISOEncoder!!.encode(cc, bb, true)
                bb.flip()
                converted = bb.remaining()
            } catch (e: Exception) {
                return -1
            }

            if (converted == 2) {
                if (!SODesDefined) {
                    newSODesDefined = true
                    ebyte!![0] = ISO_ESC
                    SODesig.copyInto(ebyte, 1)
                    index = SODesig.size + 1
                }
                if (!shiftout) {
                    newshiftout = true
                    ebyte!![index++] = ISO_SO
                }
                ebyte!![index++] = (convByte[0].toInt() and 0x7f).toByte()
                ebyte[index++] = (convByte[1].toInt() and 0x7f).toByte()
            } else {
                if (convByte[0] == SS2) {
                    if (convByte[1] == PLANE2) {
                        if (!SS2DesDefined) {
                            newSS2DesDefined = true
                            ebyte!![0] = ISO_ESC
                            SS2Desig!!.copyInto(ebyte, 1)
                            index = SS2Desig!!.size + 1
                        }
                        ebyte!![index++] = ISO_ESC
                        ebyte[index++] = ISO_SS2_7
                        ebyte[index++] = (convByte[2].toInt() and 0x7f).toByte()
                        ebyte[index++] = (convByte[3].toInt() and 0x7f).toByte()
                    } else if (convByte[1] == PLANE3) {
                        if (!SS3DesDefined) {
                            newSS3DesDefined = true
                            ebyte!![0] = ISO_ESC
                            SS3Desig!!.copyInto(ebyte, 1)
                            index = SS3Desig!!.size + 1
                        }
                        ebyte!![index++] = ISO_ESC
                        ebyte[index++] = ISO_SS3_7
                        ebyte[index++] = (convByte[2].toInt() and 0x7f).toByte()
                        ebyte[index++] = (convByte[3].toInt() and 0x7f).toByte()
                    }
                }
            }
            return index
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

            var outputSize: Int
            val outputByte = ByteArray(8)
            newshiftout = shiftout
            newSODesDefined = SODesDefined
            newSS2DesDefined = SS2DesDefined
            newSS3DesDefined = SS3DesDefined

            try {
                while (sp < sl) {
                    val c = sa[sp]
                    if (Character.isSurrogate(c)) {
                        if (sgp.parse(c, sa, sp, sl) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }

                    if (c.code < 0x80) {     // ASCII
                        if (shiftout) {
                            newshiftout = false
                            outputSize = 2
                            outputByte[0] = ISO_SI
                            outputByte[1] = (c.code and 0x7f).toByte()
                        } else {
                            outputSize = 1
                            outputByte[0] = (c.code and 0x7f).toByte()
                        }
                        if (sa[sp] == '\n') {
                            newSODesDefined = false
                            newSS2DesDefined = false
                            newSS3DesDefined = false
                        }
                    } else {
                        outputSize = unicodeToNative(c, outputByte)
                        if (outputSize == 0) {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }
                    if (dl - dp < outputSize) return CoderResultInternal.OVERFLOW

                    for (i in 0..<outputSize) da[dp++] = outputByte[i]
                    sp++
                    shiftout = newshiftout
                    SODesDefined = newSODesDefined
                    SS2DesDefined = newSS2DesDefined
                    SS3DesDefined = newSS3DesDefined
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(sp - src.arrayOffset())
                dst.position(dp - dst.arrayOffset())
            }
        }

        private fun encodeBufferLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var outputSize: Int
            val outputByte = ByteArray(8)
            newshiftout = shiftout
            newSODesDefined = SODesDefined
            newSS2DesDefined = SS2DesDefined
            newSS3DesDefined = SS3DesDefined
            var mark = src.position()

            try {
                while (src.hasRemaining()) {
                    val inputChar = src.get()
                    if (Character.isSurrogate(inputChar)) {
                        if (sgp.parse(inputChar, src) < 0) return sgp.error()
                        return sgp.unmappableResult()
                    }
                    if (inputChar.code < 0x80) {     // ASCII
                        if (shiftout) {
                            newshiftout = false
                            outputSize = 2
                            outputByte[0] = ISO_SI
                            outputByte[1] = (inputChar.code and 0x7f).toByte()
                        } else {
                            outputSize = 1
                            outputByte[0] = (inputChar.code and 0x7f).toByte()
                        }
                        if (inputChar == '\n') {
                            newSODesDefined = false
                            newSS2DesDefined = false
                            newSS3DesDefined = false
                        }
                    } else {
                        outputSize = unicodeToNative(inputChar, outputByte)
                        if (outputSize == 0) {
                            return CoderResultInternal.unmappableForLength(1)
                        }
                    }

                    if (dst.remaining() < outputSize) return CoderResultInternal.OVERFLOW
                    for (i in 0..<outputSize) dst.put(outputByte[i])
                    mark++
                    shiftout = newshiftout
                    SODesDefined = newSODesDefined
                    SS2DesDefined = newSS2DesDefined
                    SS3DesDefined = newSS3DesDefined
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
            private const val ISO_ESC: Byte = 0x1b
            private const val ISO_SI: Byte = 0x0f
            private const val ISO_SO: Byte = 0x0e
            private const val ISO_SS2_7: Byte = 0x4e
            private const val ISO_SS3_7: Byte = 0x4f

            val SS2: Byte = 0x8e.toByte()
            val PLANE2: Byte = 0xA2.toByte()
            val PLANE3: Byte = 0xA3.toByte()
        }
    }
}
