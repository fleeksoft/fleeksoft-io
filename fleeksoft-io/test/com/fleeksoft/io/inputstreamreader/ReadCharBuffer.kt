package com.fleeksoft.io.inputstreamreader

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.charset.toByteArray
import com.fleeksoft.io.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadCharBuffer {
    @Test
    fun main() {
        createBuffers().forEach { charBuffers ->
            charBuffers.forEach { buffer ->
                read(buffer)
            }
        }
    }

    fun createBuffers(): Array<Array<CharBuffer>> {
        // test both on-heap and off-heap buffers as they make use different code paths
        return arrayOf(
            arrayOf(CharBufferFactory.allocate(BUFFER_SIZE)),
        )
    }

    private fun fillBuffer(buffer: CharBuffer) {
        val filler = CharArray(BUFFER_SIZE)
        filler.fill('x')
        buffer.put(filler)
        buffer.clearExt()
    }

    fun read(buffer: CharBuffer) {
        fillBuffer(buffer)

        val charset = Charsets.forName("US-ASCII")
        InputStreamReader(ByteArrayInputStream("ABCDEFGHIJKLMNOPQRTUVWXYZ".toByteArray(charset)), charset).use { reader ->
            buffer.setLimitExt(7)
            buffer.setPositionExt(1)
            assertEquals(reader.read(buffer), 6)
            assertEquals(buffer.position(), 7)
            assertEquals(buffer.limit(), 7)

            buffer.setLimitExt(16)
            buffer.setPositionExt(8)
            assertEquals(reader.read(buffer), 8)
            assertEquals(buffer.position(), 16)
            assertEquals(buffer.limit(), 16)
        }
        buffer.clearExt()
        assertEquals(buffer.toString(), "xABCDEFxGHIJKLMNxxxxxxxx")
    }

    @Test
    fun readLeftover() {
        val b = byteArrayOf('a'.code.toByte(), 'b'.code.toByte(), 0xC2.toByte())
        val bais = ByteArrayInputStream(b)
        val r = InputStreamReader(bais, Charsets.UTF8.newDecoder().onMalformedInput(CodingErrorActionValue.IGNORE))
        var n: Int = r.read()
        assertEquals(n.toChar(), 'a')
        val c = CharArray(3)
        n = r.read(c, 0, 3)
        assertEquals(n, 1)
        assertEquals(c[0], 'b')
        n = r.read()
        assertEquals(n, -1)
    }

    companion object {
        private const val BUFFER_SIZE = 24
    }
}
