package com.fleeksoft.io.bufferedinputstream

import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.IOException
import kotlin.test.Test

class CloseStream {

    @Test
    fun main() {
        val input = BufferedInputStream(MyInputStream())

        input.read()
        input.close()

        try {
            input.read() // IOException should be thrown here
            throw RuntimeException("No exception during read on closed stream")
        } catch (e: IOException) {
            ("Test passed: IOException is thrown")
        }
    }
}

private class MyInputStream : InputStream() {
    override fun close() {
        if (status == OPEN) {
            status = CLOSED
        } else throw IOException()
    }

    override fun read(): Int {
        if (status == CLOSED) throw IOException()
        return 'a'.code.toByte().toInt()
    }

    private val OPEN = 1
    private val CLOSED = 2
    private var status = OPEN
}
