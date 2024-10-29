package com.fleeksoft.io.reader

import com.fleeksoft.io.*
import com.fleeksoft.io.exception.ReadOnlyBufferException
import kotlin.test.Test

/*
 * @test
 * @bug 8266078
 * @summary Tests that attempting to read into a read-only CharBuffer
 *          does not advance the Reader position
 * @run main ReadIntoReadOnlyBuffer
 */
class ReadIntoReadOnlyBuffer {

    @Test
    fun main() {
        var buf: CharBuffer = CharBufferFactory.allocate(8).asReadOnlyBuffer()
        var r = StringReader(THE_STRING)
        read(r, buf)
    }

    private fun read(r: Reader, b: CharBuffer) {
        try {
            r.read(b)
            throw RuntimeException("ReadOnlyBufferException expected")
        } catch (expected: ReadOnlyBufferException) {
        }

        val c = CharArray(3)
        val n: Int = r.read(c)
        if (n != c.size) {
            throw RuntimeException("Expected " + c.size + ", got " + n)
        }

        val s = c.concatToString()
        if (s != THE_STRING) {
            throw RuntimeException("Expected $THE_STRING, got $s")
        }
    }

    companion object {
        private val THE_STRING = "123"
    }
}
