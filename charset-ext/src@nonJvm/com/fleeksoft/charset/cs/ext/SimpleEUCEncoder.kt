package com.fleeksoft.charset.cs.ext

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.cs.Surrogate
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.lang.Character

abstract class SimpleEUCEncoder protected constructor(cs: Charset) : CharsetEncoder(cs, 3.0f, 4.0f) {
    protected var index1: ShortArray? = null
    protected var index2: String? = null
    protected var index2a: String? = null
    protected var index2b: String? = null
    protected var index2c: String? = null
    protected var mask1: Int = 0
    protected var mask2: Int = 0
    protected var shift: Int = 0

    private val outputByte = ByteArray(4)
    private val sgp: Surrogate.Parser = Surrogate.Parser()

    /**
     * Returns true if the given character can be converted to the
     * target character encoding.
     */
    override fun canEncode(ch: Char): Boolean {
        var index: Int
        val theChars: String

        index = index1!![((ch.code and mask1) shr shift)] + (ch.code and mask2)

        if (index < 7500) theChars = index2!!
        else if (index < 15000) {
            index = index - 7500
            theChars = index2a!!
        } else if (index < 22500) {
            index = index - 15000
            theChars = index2b!!
        } else {
            index = index - 22500
            theChars = index2c!!
        }

        if (theChars[2 * index] != '\u0000' ||
            theChars[2 * index + 1] != '\u0000'
        ) return (true)

        // only return true if input char was unicode null - all others are
        //     undefined
        return (ch == '\u0000')
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

        var index: Int
        var spaceNeeded: Int
        var i: Int

        try {
            while (sp < sl) {
                var allZeroes = true
                val inputChar = sa[sp]
                if (Character.isSurrogate(inputChar)) {
                    if (sgp.parse(inputChar, sa, sp, sl) < 0) return sgp.error()
                    return sgp.unmappableResult()
                }

                if (inputChar >= '\uFFFE') return CoderResultInternal.unmappableForLength(1)

                val theChars: String

                // We have a valid character, get the bytes for it
                index = index1!![((inputChar.code and mask1) shr shift)] + (inputChar.code and mask2)

                if (index < 7500) theChars = index2!!
                else if (index < 15000) {
                    index = index - 7500
                    theChars = index2a!!
                } else if (index < 22500) {
                    index = index - 15000
                    theChars = index2b!!
                } else {
                    index = index - 22500
                    theChars = index2c!!
                }

                var aChar: Char = theChars[2 * index]
                outputByte[0] = ((aChar.code and 0xff00) shr 8).toByte()
                outputByte[1] = (aChar.code and 0x00ff).toByte()
                aChar = theChars[2 * index + 1]
                outputByte[2] = ((aChar.code and 0xff00) shr 8).toByte()
                outputByte[3] = (aChar.code and 0x00ff).toByte()

                i = 0
                while (i < outputByte.size) {
                    if (outputByte[i].toInt() != 0x00) {
                        allZeroes = false
                        break
                    }
                    i++
                }

                if (allZeroes && inputChar != '\u0000') {
                    return CoderResultInternal.unmappableForLength(1)
                }

                var oindex = 0

                spaceNeeded = outputByte.size
                while (spaceNeeded > 1) {
                    if (outputByte[oindex++].toInt() != 0x00) break
                    spaceNeeded--
                }

                if (dp + spaceNeeded > dl) return CoderResultInternal.OVERFLOW

                i = outputByte.size - spaceNeeded
                while (i < outputByte.size) {
                    da[dp++] = outputByte[i]
                    i++
                }
                sp++
            }
            return CoderResultInternal.UNDERFLOW
        } finally {
            src.position(sp - src.arrayOffset())
            dst.position(dp - dst.arrayOffset())
        }
    }

    private fun encodeBufferLoop(
        src: CharBuffer,
        dst: ByteBuffer
    ): CoderResult {
        var index: Int
        var spaceNeeded: Int
        var i: Int
        var mark = src.position()
        try {
            while (src.hasRemaining()) {
                val inputChar = src.get()
                var allZeroes = true
                if (Character.isSurrogate(inputChar)) {
                    if (sgp.parse(inputChar, src) < 0) return sgp.error()
                    return sgp.unmappableResult()
                }

                if (inputChar >= '\uFFFE') return CoderResultInternal.unmappableForLength(1)

                val theChars: String

                // We have a valid character, get the bytes for it
                index = index1!![((inputChar.code and mask1) shr shift)] + (inputChar.code and mask2)

                if (index < 7500) theChars = index2!!
                else if (index < 15000) {
                    index = index - 7500
                    theChars = index2a!!
                } else if (index < 22500) {
                    index = index - 15000
                    theChars = index2b!!
                } else {
                    index = index - 22500
                    theChars = index2c!!
                }

                var aChar: Char = theChars[2 * index]
                outputByte[0] = ((aChar.code and 0xff00) shr 8).toByte()
                outputByte[1] = (aChar.code and 0x00ff).toByte()
                aChar = theChars[2 * index + 1]
                outputByte[2] = ((aChar.code and 0xff00) shr 8).toByte()
                outputByte[3] = (aChar.code and 0x00ff).toByte()

                i = 0
                while (i < outputByte.size) {
                    if (outputByte[i].toInt() != 0x00) {
                        allZeroes = false
                        break
                    }
                    i++
                }
                if (allZeroes && inputChar != '\u0000') {
                    return CoderResultInternal.unmappableForLength(1)
                }

                var oindex = 0

                spaceNeeded = outputByte.size
                while (spaceNeeded > 1) {
                    if (outputByte[oindex++].toInt() != 0x00) break
                    spaceNeeded--
                }
                if (dst.remaining() < spaceNeeded) return CoderResultInternal.OVERFLOW

                i = outputByte.size - spaceNeeded
                while (i < outputByte.size) {
                    dst.put(outputByte[i])
                    i++
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

    fun encode(inputChar: Char): Byte {
        return index2!![index1!![(inputChar.code and mask1) shr shift] + (inputChar.code and mask2)].code.toByte()
    }
}
