package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException

actual abstract class Buffer(
    protected var bufferPosition: Int = 0,
    protected var bufferLimit: Int,
    val cap: Int,
    protected var bufferMark: Int = -1
) {

    init {
        if (bufferMark >= 0 && bufferMark > bufferPosition) {
            throw IllegalArgumentException("mark > position: ($bufferMark > $bufferPosition)")
        }
    }

    actual fun remaining(): Int {
        val rem = bufferLimit - bufferPosition
        return if (rem > 0) rem else 0
    }

    actual fun capacity(): Int = cap
    actual fun limit(): Int = bufferLimit
    open fun limit(newLimit: Int): Buffer {
        if (newLimit > cap || newLimit < 0) {
            throw Exception("Invalid newLimit: $newLimit")
        }
        bufferLimit = newLimit
        if (bufferPosition > newLimit) bufferPosition = newLimit
        if (bufferMark > newLimit) bufferMark = -1
        return this
    }

    actual abstract fun isReadOnly(): Boolean

    actual fun position(): Int = bufferPosition
    open fun position(pos: Int): Buffer {
        if (bufferMark > pos) bufferMark = -1
        bufferPosition = pos
        return this
    }

    open fun flip(): Buffer {
        bufferLimit = bufferPosition
        bufferPosition = 0
        bufferMark = -1
        return this
    }

    open fun clear(): Buffer {
        bufferPosition = 0
        bufferLimit = cap
        bufferMark = -1
        return this
    }

    actual fun hasRemaining(): Boolean = bufferPosition < bufferLimit
    open fun mark(): Buffer {
        bufferMark = bufferPosition
        return this
    }

    fun discardMark() {
        bufferMark = -1
    }

    open fun reset(): Buffer {
        val m = bufferMark
        require(bufferMark >= 0) { "mark < 0" }
        bufferPosition = m
        return this
    }

    open fun rewind(): Buffer {
        bufferPosition = 0
        bufferMark = -1
        return this
    }

    actual abstract fun arrayOffset(): Int
    actual abstract fun array(): Any
    actual abstract fun hasArray(): Boolean
    abstract fun slice(): Buffer
    abstract fun slice(index: Int, length: Int): Buffer
    abstract fun duplicate(): Buffer

    protected fun nextPutIndex(): Int {
        val p = bufferPosition
        if (p >= bufferLimit) throw BufferOverflowException()
        bufferPosition = p + 1
        return p
    }

    protected fun nextGetIndex(): Int {
        val p = bufferPosition
        if (p >= bufferLimit) throw BufferUnderflowException()
        bufferPosition = p + 1
        return p
    }

    protected fun checkIndex(index: Int): Int {
        if (index < 0 || index >= bufferLimit) throw Exception("Invalid index: $index")
        return index
    }

    protected fun checkIndex(index: Int, nb: Int): Int {
        val length = bufferLimit - nb + 1
        if (index < 0 || index >= length)
            throw IndexOutOfBoundsException()

        return index
    }


    protected fun checkFromIndexSize(fromIndex: Int, size: Int, destSize: Int): Int {
        if ((destSize or fromIndex or size) < 0 || size > destSize - fromIndex) {
            throw BufferUnderflowException()
        }

        return fromIndex
    }

    protected fun checkFromToIndex(fromIndex: Int, toIndex: Int, length: Int): Int {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length)
            throw IndexOutOfBoundsException()
        return fromIndex
    }
}

actual fun Buffer.setPositionExt(pos: Int) {
    this.position(pos)
}

actual fun Buffer.setLimitExt(newLimit: Int) {
    this.limit(newLimit)
}

actual fun Buffer.clearExt() {
    this.clear()
}

actual fun Buffer.flipExt() {
    this.flip()
}

actual fun Buffer.rewindExt() {
    this.rewind()
}

actual fun Buffer.markExt() {
    this.mark()
}

actual fun Buffer.resetExt() {
    this.reset()
}

actual fun Buffer.sliceExt() {
    this.slice()
}

actual fun Buffer.sliceExt(index: Int, length: Int) {
    this.slice(index, length)
}