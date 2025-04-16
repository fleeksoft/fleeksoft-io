package com.fleeksoft.io.bufferedoutputstream

import com.fleeksoft.io.BufferedOutputStream
import com.fleeksoft.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class CloseStream {

    @Test
    fun main() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        
        // Write some data
        bufferedOutputStream.write(1)
        bufferedOutputStream.write(2)
        
        // At this point, the bytes should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Close the stream (should flush the buffer)
        bufferedOutputStream.close()
        
        // The data should have been flushed to the underlying stream
        assertEquals(2, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(2, bytes.size)
        assertEquals(1, bytes[0].toInt() and 0xff)
        assertEquals(2, bytes[1].toInt() and 0xff)
    }
}