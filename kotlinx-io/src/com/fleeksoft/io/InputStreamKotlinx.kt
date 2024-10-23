package com.fleeksoft.io

import kotlinx.io.Source
import kotlinx.io.readByteArray

internal class InputStreamKotlinx(private val source: Source) : InputStream() {
    private var sourceMark: Source? = null

    fun source(): Source = sourceMark ?: source

    override fun mark(readLimit: Int) {
        sourceMark = source().peek()
    }

    override fun reset() {
        sourceMark?.close()
        sourceMark = null
    }

    override fun read(): Int {
        return source().readInt()
    }

    override fun readNBytes(count: Int): ByteArray {
        val byteArray = ByteArray(count)
        var i = 0
        while (source().exhausted().not() && i < count) {
            byteArray[i] = source().readByte()
            i++
        }
        return if (i == 0) {
            byteArrayOf()
        } else if (i != count) {
            byteArray.copyOfRange(0, i)
        } else {
            byteArray
        }
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        return source().readAtMostTo(bytes, offset, endIndex = offset + length)
    }

    override fun readAllBytes(): ByteArray {
        return source().readByteArray()
    }

    fun exhausted(): Boolean {
        return source().exhausted()
    }

    override fun close() {
        return source().close()
    }
}