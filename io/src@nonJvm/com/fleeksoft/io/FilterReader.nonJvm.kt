package com.fleeksoft.io

actual abstract class FilterReader actual constructor(private val reader: Reader) : Reader() {
    actual override fun read(): Int {
        return reader.read()
    }

    actual override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        return reader.read(cbuf, off, len)
    }

    actual override fun skip(n: Long): Long {
        return reader.skip(n)
    }

    actual override fun ready(): Boolean {
        return reader.ready()
    }

    actual override fun markSupported(): Boolean {
        return reader.markSupported()
    }

    actual override fun reset() {
        reader.reset()
    }

    actual override fun close() {
        reader.close()
    }
}