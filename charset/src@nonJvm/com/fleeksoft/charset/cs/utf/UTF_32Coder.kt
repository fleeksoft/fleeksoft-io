package com.fleeksoft.charset.cs.utf

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.getInt
import com.fleeksoft.lang.Character

object UTF_32Coder {
    internal const val BOM_BIG: Int = 0xFEFF
    internal const val BOM_LITTLE: Int = -0x20000
    internal const val NONE: Int = 0
    internal const val BIG: Int = 1
    internal const val LITTLE: Int = 2

    open class Decoder(cs: Charset, private val expectedBO: Int) : CharsetDecoder(cs, 0.25f, 1.0f) {
        private var currentBO: Int

        init {
            this.currentBO = NONE
        }

        private fun getCP(src: ByteBuffer): Int {
            return if (currentBO == BIG)
                (((src.getInt() and 0xff) shl 24) or
                        ((src.getInt() and 0xff) shl 16) or
                        ((src.getInt() and 0xff) shl 8) or
                        (src.getInt() and 0xff))
            else
                ((src.getInt() and 0xff) or
                        ((src.getInt() and 0xff) shl 8) or
                        ((src.getInt() and 0xff) shl 16) or
                        ((src.getInt() and 0xff) shl 24))
        }

        override fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult {
            if (src.remaining() < 4) return CoderResultInternal.UNDERFLOW
            var mark: Int = src.position()
            var cp: Int
            try {
                if (currentBO == NONE) {
                    cp = ((src.getInt() and 0xff) shl 24) or
                            ((src.getInt() and 0xff) shl 16) or
                            ((src.getInt() and 0xff) shl 8) or
                            (src.getInt() and 0xff)
                    if (cp == BOM_BIG && expectedBO != LITTLE) {
                        currentBO = BIG
                        mark += 4
                    } else if (cp == BOM_LITTLE && expectedBO != BIG) {
                        currentBO = LITTLE
                        mark += 4
                    } else {
                        if (expectedBO == NONE) currentBO = BIG
                        else currentBO = expectedBO
                        src.position(mark)
                    }
                }
                while (src.remaining() >= 4) {
                    cp = getCP(src)
                    if (Character.isBmpCodePoint(cp)) {
                        if (!dst.hasRemaining()) return CoderResultInternal.OVERFLOW
                        mark += 4
                        dst.put(cp.toChar())
                    } else if (Character.isValidCodePoint(cp)) {
                        if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                        mark += 4
                        dst.put(Character.highSurrogate(cp))
                        dst.put(Character.lowSurrogate(cp))
                    } else {
                        return CoderResultInternal.malformedForLength(4)
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun implReset() {
            currentBO = NONE
        }
    }

    open class Encoder(cs: Charset, private val byteOrder: Int, doBOM: Boolean) : CharsetEncoder(
        cs, 4.0f,
        if (doBOM) 8.0f else 4.0f,
        if (byteOrder == BIG)
            byteArrayOf(0.toByte(), 0.toByte(), 0xff.toByte(), 0xfd.toByte())
        else
            byteArrayOf(0xfd.toByte(), 0xff.toByte(), 0.toByte(), 0.toByte())
    ) {
        private var doBOM = false
        private var doneBOM = true

        protected fun put(cp: Int, dst: ByteBuffer) {
            if (byteOrder == BIG) {
                dst.put((cp shr 24).toByte())
                dst.put((cp shr 16).toByte())
                dst.put((cp shr 8).toByte())
                dst.put(cp.toByte())
            } else {
                dst.put(cp.toByte())
                dst.put((cp shr 8).toByte())
                dst.put((cp shr 16).toByte())
                dst.put((cp shr 24).toByte())
            }
        }

        init {
            this.doBOM = doBOM
            this.doneBOM = !doBOM
        }

        override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
            var mark: Int = src.position()
            if (!doneBOM && src.hasRemaining()) {
                if (dst.remaining() < 4) return CoderResultInternal.OVERFLOW
                put(BOM_BIG, dst)
                doneBOM = true
            }
            try {
                while (src.hasRemaining()) {
                    val c: Char = src.get()
                    if (!Character.isSurrogate(c)) {
                        if (dst.remaining() < 4) return CoderResultInternal.OVERFLOW
                        mark++
                        put(c.code, dst)
                    } else if (Character.isHighSurrogate(c)) {
                        if (!src.hasRemaining()) return CoderResultInternal.UNDERFLOW
                        val low: Char = src.get()
                        if (Character.isLowSurrogate(low)) {
                            if (dst.remaining() < 4) return CoderResultInternal.OVERFLOW
                            mark += 2
                            put(Character.toCodePoint(c, low), dst)
                        } else {
                            return CoderResultInternal.malformedForLength(1)
                        }
                    } else {
                        // assert Character.isLowSurrogate(c);
                        return CoderResultInternal.malformedForLength(1)
                    }
                }
                return CoderResultInternal.UNDERFLOW
            } finally {
                src.position(mark)
            }
        }

        override fun implReset() {
            doneBOM = !doBOM
        }
    }
}