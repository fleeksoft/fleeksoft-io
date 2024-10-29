package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import com.fleeksoft.io.internal.assert

internal class HeapCharBuffer(
    charArray: CharArray,
    position: Int,
    offset: Int,
    limit: Int,
    mark: Int = -1,
    cap: Int = charArray.size,
    readOnly: Boolean = false
) : CharBuffer(
    charArray = charArray,
    position = position,
    offset = offset,
    limit = limit,
    mark = mark,
    cap = cap,
    readOnly = readOnly,
) {
    override fun isReadOnly(): Boolean = readOnly

    override fun slice(): CharBuffer {
        val pos = this.position()
        val lim = this.limit()
        val rem = (if (pos <= lim) lim - pos else 0)
        return HeapCharBuffer(
            charArray,
            mark = -1,
            position = 0,
            limit = rem,
            cap = rem,
            offset = pos + bufferOffset
        )
    }

    override fun slice(index: Int, length: Int): CharBuffer {
        return HeapCharBuffer(
            charArray,
            mark = -1,
            position = 0,
            limit = length,
            cap = length,
            offset = index + bufferOffset
        )
    }

    override fun duplicate(): CharBuffer {
        return HeapCharBuffer(
            charArray = charArray,
            position = bufferPosition,
            offset = bufferOffset,
            limit = bufferLimit,
            mark = bufferMark,
            cap = capacity(),
            readOnly = readOnly,
        )
    }

    override fun asReadOnlyBuffer(): CharBuffer {
        return HeapCharBuffer(
            charArray = charArray,
            position = bufferPosition,
            offset = bufferOffset,
            limit = bufferLimit,
            mark = bufferMark,
            cap = capacity(),
            readOnly = true,
        )
    }

    private fun ix(i: Int): Int {
        return i + bufferOffset
    }

    override fun get(): Char = charArray[ix(nextGetIndex())]

    override fun get(index: Int): Char = charArray[ix(checkIndex(index))]

    override fun get(dstCharArray: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(off, len, dstCharArray.size)
        val pos = position()
        if (len > limit() - pos) {
            throw BufferUnderflowException()
        }
        val p = ix(pos)
        charArray.copyInto(dstCharArray, destinationOffset = off, startIndex = p, endIndex = p + len)
        position(pos + len)
        return this
    }

    override fun get(index: Int, dstCharArray: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, dstCharArray.size)
        val i = ix(index)
        charArray.copyInto(
            destination = dstCharArray,
            destinationOffset = off,
            startIndex = i,
            endIndex = i + len
        )
        return this
    }

    override fun put(c: Char): CharBuffer {
        charArray[ix(nextPutIndex())] = c
        return this
    }

    override fun put(index: Int, c: Char): CharBuffer {
        charArray[ix(checkIndex(index))] = c
        return this
    }

    override fun put(src: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(off, len, src.size)
        val pos = position()
        if (len > limit() - pos) {
            throw BufferOverflowException()
        }
        src.copyInto(charArray, ix(pos), off, off + len)
        position(pos + len)
        return this
    }

    override fun put(index: Int, src: CharArray, off: Int, len: Int): CharBuffer {
        checkFromIndexSize(index, len, limit())
        checkFromIndexSize(off, len, src.size)
        src.copyInto(charArray, ix(index), off, off + len)
        return this
    }


    override fun put(src: String, start: Int, end: Int): CharBuffer {
        val length = end - start
        checkFromIndexSize(start, length, src.length)
        val pos = position()
        val lim = limit()
        val rem = if (pos <= lim) lim - pos else 0
        if (length > rem) throw BufferOverflowException()
        src.toCharArray().copyInto(charArray, ix(pos), start, end)
        position(pos + length)
        return this
    }

    override fun compact(): CharBuffer {
        val pos = position()
        val lim = limit()
        assert(pos <= lim)
        val rem = if (pos <= lim) lim - pos else 0
        val p = ix(pos)
        charArray.copyInto(charArray, ix(0), p, p + rem)
        position(rem)
        limit(capacity())
        discardMark()
        return this
    }

    override fun toString(start: Int, end: Int): String {
        return charArray.concatToString(position(), limit())
    }

}