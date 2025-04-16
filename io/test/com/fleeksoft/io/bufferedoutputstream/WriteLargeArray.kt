package com.fleeksoft.io.bufferedoutputstream

import com.fleeksoft.io.BufferedOutputStream
import com.fleeksoft.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteLargeArray {

    @Test
    fun main() {
        val outputStream = ByteArrayOutputStream()
        // Create a buffered output stream with a small buffer size
        val bufferedOutputStream = BufferedOutputStream(outputStream, 3)
        
        // Create a byte array larger than the buffer
        val data = ByteArray(10) { i -> (i + 1).toByte() }
        
        // Write the large byte array
        bufferedOutputStream.write(data, 0, data.size)
        
        // The entire array should be written directly to the underlying stream
        // without using the buffer, since it's larger than the buffer size
        assertEquals(10, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(10, bytes.size)
        for (i in 0 until 10) {
            assertEquals(i + 1, bytes[i].toInt() and 0xff)
        }
    }
}