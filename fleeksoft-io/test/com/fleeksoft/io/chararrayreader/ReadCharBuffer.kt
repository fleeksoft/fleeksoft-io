package com.fleeksoft.io.chararrayreader

import com.fleeksoft.io.CharArrayReader
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory
import com.fleeksoft.io.clearExt
import com.fleeksoft.io.setLimitExt
import com.fleeksoft.io.setPositionExt
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
        // test both on-heap and off-heap buffers as they may use different code paths
        return arrayOf(
            arrayOf(CharBufferFactory.allocate(BUFFER_SIZE))
        )
    }

    fun read(buffer: CharBuffer) {
        fillBuffer(buffer)

        CharArrayReader("ABCD".toCharArray()).use { reader ->
            buffer.setLimitExt(3)
            buffer.setPositionExt(1)
            assertEquals(reader.read(buffer), 2)
            assertEquals(buffer.position(), 3)
            assertEquals(buffer.limit(), 3)

            buffer.setLimitExt(7)
            buffer.setPositionExt(4)
            assertEquals(reader.read(buffer), 2)
            assertEquals(buffer.position(), 6)
            assertEquals(buffer.limit(), 7)
            assertEquals(reader.read(buffer), -1)
        }
        buffer.clearExt()
        assertEquals(buffer.toString(), "xABxCDx")
    }

    private fun fillBuffer(buffer: CharBuffer) {
        val filler = CharArray(BUFFER_SIZE)
        filler.fill('x')
        buffer.put(filler)
        buffer.clearExt()
    }

    companion object {
        private const val BUFFER_SIZE = 7
    }
}
