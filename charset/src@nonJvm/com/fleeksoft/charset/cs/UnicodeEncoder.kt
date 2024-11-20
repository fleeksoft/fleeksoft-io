package com.fleeksoft.charset.cs

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.CoderResult
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.lang.Character

/**
 * Base class for different flavors of UTF-16 encoders
 */
abstract class UnicodeEncoder protected constructor(cs: Charset, bo: Int, private var needsMark: Boolean) : CharsetEncoder(
    cs, 2.0f,  // Four bytes max if you need a BOM
    if (needsMark) 4.0f else 2.0f,  // Replacement depends upon byte order
    (if (bo == BIG)
        byteArrayOf(0xff.toByte(), 0xfd.toByte())
    else
        byteArrayOf(0xfd.toByte(), 0xff.toByte()))
) {
    private val byteOrder: Int = bo /* Byte order in use */
    private val usesMark: Boolean = needsMark /* Write an initial BOM */

    private fun put(c: Char, dst: ByteBuffer) {
        if (byteOrder == BIG) {
            dst.put((c.code shr 8).toByte())
            dst.put((c.code and 0xff).toByte())
        } else {
            dst.put((c.code and 0xff).toByte())
            dst.put((c.code shr 8).toByte())
        }
    }

    private val sgp = Surrogate.Parser()

    override fun encodeLoop(src: CharBuffer, dst: ByteBuffer): CoderResult {
        var mark: Int = src.position()

        if (needsMark && src.hasRemaining()) {
            if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
            put(BYTE_ORDER_MARK, dst)
            needsMark = false
        }
        try {
            while (src.hasRemaining()) {
                val c: Char = src.get()
                if (!Character.isSurrogate(c)) {
                    if (dst.remaining() < 2) return CoderResultInternal.OVERFLOW
                    mark++
                    put(c, dst)
                    continue
                }
                val d = sgp.parse(c, src)
                if (d < 0) return sgp.error()
                if (dst.remaining() < 4) return CoderResultInternal.OVERFLOW
                mark += 2
                put(Character.highSurrogate(d), dst)
                put(Character.lowSurrogate(d), dst)
            }
            return CoderResultInternal.UNDERFLOW
        } finally {
            src.position(mark)
        }
    }

    override fun implReset() {
        needsMark = usesMark
    }

    override fun canEncode(c: Char): Boolean {
        return !Character.isSurrogate(c)
    }

    companion object {
        const val BYTE_ORDER_MARK: Char = '\uFEFF'
        const val REVERSED_MARK: Char = '\uFFFE'

        const val BIG: Int = 0
        const val LITTLE: Int = 1
    }
}