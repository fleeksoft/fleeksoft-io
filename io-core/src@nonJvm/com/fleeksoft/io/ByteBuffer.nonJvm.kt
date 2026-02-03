package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import com.fleeksoft.io.exception.ReadOnlyBufferException

actual abstract class ByteBuffer(
    protected val byteArray: ByteArray,
    position: Int,
    offset: Int,
    limit: Int,
    mark: Int = -1,
    cap: Int = byteArray.size,
    protected val readOnly: Boolean = false
) : Buffer(
    bufferPosition = position,
    bufferLimit = limit,
    cap = cap,
    bufferMark = mark
), Comparable<ByteBuffer> {
    protected var bufferOffset: Int = offset
    abstract override fun slice(): ByteBuffer
    abstract override fun slice(index: Int, length: Int): ByteBuffer
    abstract override fun duplicate(): ByteBuffer
    actual abstract fun asReadOnlyBuffer(): ByteBuffer
    actual abstract fun get(): Byte
    actual abstract fun get(index: Int): Byte
    actual fun get(dst: ByteArray): ByteBuffer {
        return get(dst, 0, dst.size)
    }

    actual fun get(index: Int, dst: ByteArray): ByteBuffer {
        return get(index, dst, 0, dst.size)
    }

    actual open fun get(dst: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(off, len, dst.size)
        val pos = position()
        if (len > limit() - pos)
            throw BufferUnderflowException()

        getArray(pos, dst, off, len)

        position(pos + len)
        return this
    }

    actual open fun get(index: Int, dst: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, dst.size)

        getArray(index, dst, off, len)

        return this
    }

    private fun getArray(index: Int, dst: ByteArray, off: Int, len: Int) {
        val end = off + len
        for (i in off until end) {
            dst[i] = get(index + (i - off))
        }
    }

    actual abstract fun put(b: Byte): ByteBuffer
    actual abstract fun put(index: Int, b: Byte): ByteBuffer
    actual fun put(src: ByteArray): ByteBuffer {
        return put(src, 0, src.size)
    }

    actual fun put(index: Int, src: ByteArray): ByteBuffer {
        return put(index, src, 0, src.size)
    }

    actual open fun put(src: ByteArray, off: Int, len: Int): ByteBuffer {
        if (isReadOnly()) {
            throw ReadOnlyBufferException()
        }
        checkFromIndexSize(off, len, src.size)
        val pos = position()
        if (len > limit() - pos) {
            throw BufferOverflowException()
        }

        putArray(pos, src, off, len)

        position(pos + len)
        return this
    }

    actual open fun put(index: Int, src: ByteArray, off: Int, len: Int): ByteBuffer {
        if (isReadOnly())
            throw ReadOnlyBufferException()
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.size)

        putArray(index, src, off, len)

        return this
    }

    private fun putArray(index: Int, src: ByteArray, off: Int, len: Int) {
        val end = off + len
        for (i in off until end) {
            this.put(index + (i - off), src[i])
        }
    }

    actual fun put(src: ByteBuffer): ByteBuffer {
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

    actual fun put(index: Int, src: ByteBuffer, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.limit())
        if (isReadOnly())
            throw ReadOnlyBufferException()

        putBuffer(index, src, off, len)

        return this
    }

    fun copyInto(dst: ByteBuffer, destOffset: Int = 0, srcPos: Int = 0, length: Int): ByteBuffer {
        byteArray.copyInto(
            destination = dst.byteArray,
            destinationOffset = destOffset,
            startIndex = srcPos,
            endIndex = srcPos + length
        )
        return this
    }

    private fun putBuffer(pos: Int, src: ByteBuffer, srcPos: Int, srcRem: Int): ByteBuffer {
        return src.copyInto(this, destOffset = ix(pos), srcPos, srcRem)
    }

    protected fun ix(i: Int): Int {
        return i + bufferOffset
    }


    actual final override fun array(): ByteArray {
        if (isReadOnly())
            throw ReadOnlyBufferException()

        return byteArray
    }

    actual final override fun hasArray(): Boolean = true

    actual final override fun arrayOffset(): Int {
        if (isReadOnly())
            throw ReadOnlyBufferException()

        return bufferOffset
    }

    override fun position(pos: Int): ByteBuffer {
        super.position(pos)
        return this
    }

    override fun limit(newLimit: Int): ByteBuffer {
        super.limit(newLimit)
        return this
    }

    override fun mark(): ByteBuffer {
        super.mark()
        return this
    }

    override fun reset(): ByteBuffer {
        super.reset()
        return this
    }

    override fun clear(): ByteBuffer {
        super.clear()
        return this
    }

    override fun flip(): ByteBuffer {
        super.flip()
        return this
    }

    override fun rewind(): ByteBuffer {
        super.rewind()
        return this
    }

    actual abstract fun compact(): ByteBuffer


    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ByteBuffer) {
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

    actual override fun compareTo(other: ByteBuffer): Int {
        val thisPos = this.position()
        val thisRem = this.limit() - thisPos
        val thatPos = other.position()
        val thatRem = other.limit() - thatPos
        val length = minOf(thisRem, thatRem)
        if (length < 0) {
            return -1
        }
        val i = mismatch(this, thisPos, other, thatPos, length)
        if (i >= 0) {
            return compare(this.get(thisPos + i), other.get(thatPos + i))
        }
        return thisRem - thatRem
    }

    override fun hashCode(): Int {
        var h = 1
        val p = position()
        for (i in limit() - 1 downTo p) {
            h = 31 * h + get(i).toInt()
        }
        return h
    }


    companion object {
        private fun mismatch(a: ByteBuffer, aOff: Int, b: ByteBuffer, bOff: Int, length: Int): Int {
            var i = 0
            while (i < length) {
                if (a.get(aOff + i) != b.get(bOff + i)) {
                    return i
                }
                i++
            }
            return -1
        }

        fun compare(x: Byte, y: Byte): Int {
            return x.compareTo(y)
        }
    }

}

actual fun ByteBuffer.duplicateExt(): ByteBuffer = this.duplicate()