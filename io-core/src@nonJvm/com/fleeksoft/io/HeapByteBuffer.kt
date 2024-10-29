package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import com.fleeksoft.io.internal.assert

internal class HeapByteBuffer(
    byteArray: ByteArray,
    position: Int,
    offset: Int,
    limit: Int,
    mark: Int = -1,
    cap: Int = byteArray.size,
    readOnly: Boolean = false
) : ByteBuffer(
    byteArray = byteArray,
    position = position,
    offset = offset,
    limit = limit,
    mark = mark,
    cap = cap,
    readOnly = readOnly,
) {
    override fun isReadOnly(): Boolean = readOnly

    override fun slice(): ByteBuffer {
        val pos = this.position()
        val lim = this.limit()
        val rem = (if (pos <= lim) lim - pos else 0)
        return HeapByteBuffer(
            byteArray,
            mark = -1,
            position = 0,
            limit = rem,
            cap = rem,
            offset = pos + bufferOffset
        )
    }

    override fun slice(index: Int, length: Int): ByteBuffer {
        return HeapByteBuffer(
            byteArray,
            mark = -1,
            position = 0,
            limit = length,
            cap = length,
            offset = index + bufferOffset
        )
    }

    override fun duplicate(): ByteBuffer {
        return HeapByteBuffer(
            byteArray = byteArray,
            position = bufferPosition,
            offset = bufferOffset,
            limit = bufferLimit,
            mark = bufferMark,
            cap = capacity(),
            readOnly = readOnly,
        )
    }

    override fun asReadOnlyBuffer(): ByteBuffer {
        return HeapByteBuffer(
            byteArray = byteArray,
            position = bufferPosition,
            offset = bufferOffset,
            limit = bufferLimit,
            cap = capacity(),
            mark = bufferMark,
            readOnly = true,
        )
    }

    override fun get(): Byte {
        return byteArray[ix(nextGetIndex())]
    }

    override fun get(index: Int): Byte {
        return byteArray[ix(checkIndex(index))]
    }

    override fun get(dst: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(off, len, dst.size)
        val pos = position()
        if (len > limit() - pos) {
            throw BufferUnderflowException()
        }
        val p = ix(pos)
        byteArray.copyInto(dst, destinationOffset = off, startIndex = p, endIndex = p + len)
        position(pos + len)
        return this
    }

    override fun get(index: Int, dst: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, dst.size)
        val i = ix(index)
        byteArray.copyInto(
            destination = dst,
            destinationOffset = off,
            startIndex = i,
            endIndex = i + len
        )
        return this
    }

    override fun put(b: Byte): ByteBuffer {
        byteArray[ix(nextPutIndex())] = b
        return this
    }

    override fun put(index: Int, b: Byte): ByteBuffer {
        byteArray[ix(checkIndex(index))] = b
        return this
    }

    override fun put(src: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(off, len, src.size)
        val pos = position()
        if (len > limit() - pos) {
            throw BufferOverflowException()
        }
        src.copyInto(byteArray, ix(pos), off, off + len)
        position(pos + len)
        return this
    }

    override fun put(index: Int, src: ByteArray, off: Int, len: Int): ByteBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.size)
        src.copyInto(byteArray, ix(index), off, off + len)
        return this
    }


    override fun compact(): ByteBuffer {
        val pos = position()
        val lim = limit()
        assert(pos <= lim)
        val rem = if (pos <= lim) lim - pos else 0
        val p = ix(pos)
        byteArray.copyInto(byteArray, ix(0), p, p + rem)
        position(rem)
        limit(capacity())
        discardMark()
        return this
    }

}