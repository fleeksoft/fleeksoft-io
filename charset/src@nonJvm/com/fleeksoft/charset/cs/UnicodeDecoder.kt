package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

internal abstract class UnicodeDecoder(cs: Charset, private var currentByteOrder: Int) : CharsetDecoder(cs, 0.5f, 1.0f) {
    private val expectedByteOrder: Int = currentByteOrder
    private var defaultByteOrder = BIG

    constructor(cs: Charset, bo: Int, defaultBO: Int) : this(cs, bo) {
        defaultByteOrder = defaultBO
    }

    private fun decode(b1: Int, b2: Int): Char {
        if (currentByteOrder == BIG) return ((b1 shl 8) or b2).toChar()
        else return ((b2 shl 8) or b1).toChar()
    }

    override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
        var mark: Int = src.position()

        try {
            while (src.remaining() > 1) {
                val b1: Int = src.getInt() and 0xff
                val b2: Int = src.getInt() and 0xff

                // Byte Order Mark interpretation
                if (currentByteOrder == NONE) {
                    val c = ((b1 shl 8) or b2).toChar()
                    if (c == BYTE_ORDER_MARK) {
                        currentByteOrder = BIG
                        mark += 2
                        continue
                    } else if (c == REVERSED_MARK) {
                        currentByteOrder = LITTLE
                        mark += 2
                        continue
                    } else {
                        currentByteOrder = defaultByteOrder
                        // FALL THROUGH to process b1, b2 normally
                    }
                }

                val c = decode(b1, b2)

                // Surrogates
                if (Character.isSurrogate(c)) {
                    if (Character.isHighSurrogate(c)) {
                        if (src.remaining() < 2) return CoderResultInternal.UNDERFLOW
                        val c2 = decode(src.getInt() and 0xff, src.getInt() and 0xff)
                        if (!Character.isLowSurrogate(c2)) return CoderResultInternal.malformedForLength(4)
                        if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                        mark += 4
                        dst.put(c)
                        dst.put(c2)
                        continue
                    }
                    // Unpaired low surrogate
                    return CoderResultInternal.malformedForLength(2)
                }

                if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                mark += 2
                dst.put(c)
            }
            return CoderResultInternal.UNDERFLOW
        } finally {
            src.position(mark)
        }
    }

    override fun implReset() {
        currentByteOrder = expectedByteOrder
    }

    companion object {
        val BYTE_ORDER_MARK: Char = 0xfeff.toChar()
        val REVERSED_MARK: Char = 0xfffe.toChar()

        const val NONE: Int = 0
        const val BIG: Int = 1
        const val LITTLE: Int = 2
    }
}