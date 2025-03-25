package com.fleeksoft.io.bufferedoutputstream

import com.fleeksoft.io.BufferedOutputStream
import com.fleeksoft.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class BufferOverflow {

    @Test
    fun main() {
        val outputStream = ByteArrayOutputStream()
        // Create a buffered output stream with a small buffer size
        val bufferedOutputStream = BufferedOutputStream(outputStream, 3)
        
        // Write more bytes than the buffer can hold
        bufferedOutputStream.write(1)
        bufferedOutputStream.write(2)
        bufferedOutputStream.write(3)
        bufferedOutputStream.write(4) // This should cause the buffer to be flushed
        
        // The first 3 bytes should have been flushed to the underlying stream
        assertEquals(3, outputStream.size())
        
        // Flush the remaining byte
        bufferedOutputStream.flush()
        assertEquals(4, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(4, bytes.size)
        assertEquals(1, bytes[0].toInt() and 0xff)
        assertEquals(2, bytes[1].toInt() and 0xff)
        assertEquals(3, bytes[2].toInt() and 0xff)
        assertEquals(4, bytes[3].toInt() and 0xff)
    }
}