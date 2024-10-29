package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import com.fleeksoft.io.exception.ReadOnlyBufferException
import com.fleeksoft.io.internal.assert
import com.fleeksoft.lang.Character

actual abstract class CharBuffer(
    protected val charArray: CharArray,
    position: Int,
    offset: Int,
    limit: Int,
    mark: Int = -1,
    cap: Int = charArray.size,
    protected val readOnly: Boolean = false
) : Buffer(
    bufferPosition = position,
    bufferLimit = limit,
    cap = cap,
    bufferMark = mark
), Comparable<CharBuffer>, Appendable, Readable {
    protected var bufferOffset: Int = offset
    abstract override fun slice(): CharBuffer
    abstract override fun slice(index: Int, length: Int): CharBuffer
    abstract override fun duplicate(): CharBuffer
    actual abstract fun asReadOnlyBuffer(): CharBuffer


    actual override fun read(cb: CharBuffer): Int {
        val limit = limit()
        val pos = position()
        val remaining = limit - pos
        assert(remaining >= 0)
        if (remaining <= 0) return -1

        val targetRemaining = cb.remaining()
        assert(targetRemaining >= 0)
        if (targetRemaining <= 0) return 0

        val n = minOf(remaining, targetRemaining)

        if (targetRemaining < remaining) limit(pos + n)
        try {
            if (n > 0) cb.put(this)
        } finally {
            limit(limit)
        }
        return n
    }

    actual abstract fun get(): Char
    abstract fun get(index: Int): Char

    actual open fun get(dstCharArray: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(off, len, dstCharArray.size)
        val pos = position()
        if (len > limit() - pos)
            throw BufferUnderflowException()

        getArray(pos, dstCharArray, off, len)

        position(pos + len)
        return this
    }

    actual open fun get(index: Int, dstCharArray: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, dstCharArray.size)

        getArray(index, dstCharArray, off, len);

        return this
    }

    actual fun get(dstCharArray: CharArray): CharBuffer {
        return get(dstCharArray, 0, dstCharArray.size)
    }

    actual fun get(index: Int, dstCharArray: CharArray): CharBuffer {
        return get(index, dstCharArray, 0, dstCharArray.size)
    }

    private fun getArray(index: Int, dst: CharArray, off: Int, len: Int): CharBuffer {
        val end = off + len
        for (i in off until end) {
            dst[i] = get(index + i - off)
        }

        return this
    }

    actual abstract fun put(c: Char): CharBuffer
    actual abstract fun put(index: Int, c: Char): CharBuffer
    actual open fun put(src: CharArray, off: Int, len: Int): CharBuffer {
        if (isReadOnly())
            throw ReadOnlyBufferException()
        checkFromIndexSize(off, len, src.size)
        val pos = position()
        if (len > limit() - pos)
            throw BufferOverflowException()

        putArray(pos, src, off, len)

        position(pos + len)
        return this
    }

    actual open fun put(index: Int, src: CharArray, off: Int, len: Int): CharBuffer {
        if (isReadOnly())
            throw ReadOnlyBufferException()
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.size)

        putArray(index, src, off, len)

        return this
    }

    actual fun put(src: CharArray): CharBuffer {
        return put(src, 0, src.size)
    }

    actual fun put(index: Int, src: CharArray): CharBuffer {
        return put(index, src, 0, src.size)
    }

    private fun putArray(index: Int, src: CharArray, off: Int, len: Int) {
        val end = off + len
        for (i in off until end) {
            val j = index + (i - off)
            this.put(j, src[i])
        }
    }

    actual open fun put(src: CharBuffer): CharBuffer {
        if (isReadOnly()) {
            throw ReadOnlyBufferException()
        }

        val srcPos = src.position()
        val srcLim = src.limit()
        val srcRem = if (srcPos <= srcLim) srcLim - srcPos else 0
        val pos = position()
        val lim = limit()
        val rem = if (pos <= lim) lim - pos else 0

        if (srcRem > rem) {
            throw BufferOverflowException()
        }

        putBuffer(pos, src, srcPos, srcRem)

        position(pos + srcRem)
        src.position(srcPos + srcRem)

        return this
    }

    actual open fun put(index: Int, src: CharBuffer, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.limit())
        if (isReadOnly())
            throw ReadOnlyBufferException()

        putBuffer(index, src, off, len)

        return this
    }


    private fun putBuffer(pos: Int, src: CharBuffer, srcPos: Int, n: Int): CharBuffer {
        val posMax = pos + n
        for (i in pos until posMax) {
            val j = srcPos + (i - pos)
            put(i, src.get(j))
        }

        return this
    }

    actual fun put(src: String): CharBuffer {
        return put(src, 0, src.length)
    }

    actual open fun put(src: String, start: Int, end: Int): CharBuffer {
        checkFromIndexSize(start, end - start, src.length)
        if (isReadOnly())
            throw ReadOnlyBufferException()
        if (end - start > remaining())
            throw BufferOverflowException()
        for (i in start until end) {
            this.put(src[i])
        }
        return this
    }

    actual final override fun array(): CharArray {
        if (readOnly)
            throw ReadOnlyBufferException()
        return charArray
    }

    actual final override fun hasArray(): Boolean = true

    actual final override fun arrayOffset(): Int {
        if (readOnly)
            throw ReadOnlyBufferException()
        return bufferOffset
    }

    final override fun position(pos: Int): CharBuffer {
        super.position(pos)
        return this
    }

    final override fun limit(newLimit: Int): CharBuffer {
        super.limit(newLimit)
        return this
    }

    final override fun mark(): CharBuffer {
        super.mark()
        return this
    }

    final override fun reset(): CharBuffer {
        super.reset()
        return this
    }

    final override fun clear(): CharBuffer {
        super.clear()
        return this
    }

    final override fun flip(): CharBuffer {
        super.flip()
        return this
    }

    final override fun rewind(): CharBuffer {
        super.rewind()
        return this
    }

    actual abstract fun compact(): CharBuffer


    override fun hashCode(): Int {
        var h = 1
        val p = position()
        for (i in limit() - 1 downTo p)
            h = 31 * h + get(i).code
        return h
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is CharBuffer) {
            return false
        }
        val that = other
        val thisPos = this.position()
        val thisRem = this.limit() - thisPos
        val thatPos = that.position()
        val thatRem = that.limit() - thatPos
        if (thisRem < 0 || thisRem != thatRem) {
            return false
        }
        return mismatch(this, thisPos, that, thatPos, thisRem) < 0
    }

    actual override fun compareTo(other: CharBuffer): Int {
        val thisPos = position()
        val thisRem = limit() - thisPos
        val thatPos = other.position()
        val thatRem = other.limit() - thatPos
        val length = minOf(thisRem, thatRem)
        if (length < 0) return -1
        val i = mismatch(this, thisPos, other, thatPos, length)
        return if (i >= 0) Character.compare(get(thisPos + i), other.get(thatPos + i)) else thisRem - thatRem
    }

    actual override fun toString(): String {
        return toString(position(), limit())
    }

    protected abstract fun toString(start: Int, end: Int): String


    // --- Methods to support CharSequence ---
    /*override val length: Int
        get() = remaining()

    // FIXME: originaly it was override in kotlin with get(index) but that is not absolute get in charbuffer and this is relative get
    fun charAt(index: Int): Char {
        return get(position() + checkIndex(index, 1))
    }*/

    // --- Methods to support Appendable ---

    actual override fun append(value: CharSequence?): CharBuffer {
        return when (value) {
            null -> put("null")
            is CharBuffer -> put(value)
            else -> put(value.toString())
        }
    }

    actual override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): CharBuffer {
        if (value is CharBuffer) {
            checkFromToIndex(startIndex, endIndex, value.length)

            val length = endIndex - startIndex
            val pos = position()
            val lim = limit()
            val rem = if (pos <= lim) lim - pos else 0
            if (length > rem) throw BufferOverflowException()

            put(pos, value, startIndex, length)
            position(pos + length)
            return this
        }
        val cs = value?.toString() ?: "null"
        return put(cs.subSequence(startIndex, endIndex).toString())
    }

    actual override fun append(value: Char): CharBuffer {
        return put(value)
    }

    companion object {
        // FIXME: not tested
        private fun mismatch(a: CharBuffer, aOff: Int, b: CharBuffer, bOff: Int, length: Int): Int {
            var i = 0
            while (i < length) {
                if (a.get(aOff + i) != b.get(bOff + i)) {
                    return i
                }
                i++
            }
            return -1
        }
    }

}

actual fun CharBuffer.duplicateExt(): CharBuffer = this.duplicate()
actual fun CharBuffer.getChar(index: Int): Char = this.get(index)