package com.fleeksoft.io.reader

import com.fleeksoft.io.*
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.exception.UncheckedIOException
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadCharBuffer {

    @Test
    fun testStringReader() {
        createBuffers().forEach { bufferArray ->
            bufferArray.forEach { charBuffer ->
                read(charBuffer)
            }
        }
    }

    @Test
    fun readZeroLength() {
        val buf = charArrayOf(1.toChar(), 2.toChar(), 3.toChar())
        val r = BufferedReader(CharArrayReader(buf))
        var n = -1
        try {
            n = r.read(CharBufferFactory.allocate(0))
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
        assertEquals(n, 0)
    }

    fun createBuffers(): Array<Array<CharBuffer>> {
        // test both on-heap and off-heap buffers as they make use different code paths
        return arrayOf(
            arrayOf(CharBufferFactory.allocate(BUFFER_SIZE)),
        )
    }

    fun read(buffer: CharBuffer) {
        fillBuffer(buffer)

        val input = StringBuilder(BUFFER_SIZE - 2 + 1)
        input.append("ABCDEF")
        for (i in 0..8191) {
            input.append('y')
        }
        input.append("GH")

        UnoptimizedStringReader(input.toString()).use { reader ->
            // put only between position and limit in the target buffer
            var limit = 1 + 6
            buffer.setLimitExt(limit)
            buffer.setPositionExt(1)
            assertEquals(reader.read(buffer), 6)
            assertEquals(buffer.position(), limit)
            assertEquals(buffer.limit(), limit)

            // read the full temporary buffer
            // and then accurately reduce the next #read call
            limit = 8 + 8192 + 1
            buffer.setLimitExt(8 + 8192 + 1)
            buffer.setPositionExt(8)
            assertEquals(reader.read(buffer), 8192 + 1)
            assertEquals(buffer.position(), limit)
            assertEquals(buffer.limit(), limit)

            assertEquals(reader.read(), 'H'.code)
            assertEquals(reader.read(), -1)
        }
        buffer.clearExt()
        val expected = StringBuilder(BUFFER_SIZE)
        expected.append("xABCDEFx")
        for (i in 0..8191) {
            expected.append('y')
        }
        expected.append("Gx")
        assertEquals(buffer.toString(), expected.toString())
    }

    private fun fillBuffer(buffer: CharBuffer) {
        val filler = CharArray(buffer.remaining())
        filler.fill('x')
        buffer.put(filler)
        buffer.clearExt()
    }

    /**
     * Unoptimized version of StringReader in case StringReader overrides
     * #read(CharBuffer)
     */
    internal class UnoptimizedStringReader(private val str: String) : Reader() {
        private var next = 0
        private val lock = SynchronizedObject()

        override fun read(): Int {
            synchronized(lock) {
                if (next >= str.length) return -1
                return str[next++].code
            }
        }

        override fun read(cbuf: CharArray, off: Int, len: Int): Int {
            synchronized(lock) {
                if (next >= str.length) return -1
                val n: Int = min(str.length - next, len)
                str.toCharArray(next, next + n).copyInto(cbuf, off)
                next += n
                return n
            }
        }

        override fun close() {
        }
    }

    companion object {
        private const val BUFFER_SIZE = 8 + 8192 + 2
    }
}
