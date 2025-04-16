package com.fleeksoft.io.bufferedoutputstream

import com.fleeksoft.io.BufferedOutputStream
import com.fleeksoft.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteAndFlush {

    @Test
    fun main() {
        // Test writing a single byte
        testWriteSingleByte()
        
        // Test writing a byte array
        testWriteByteArray()
        
        // Test writing a partial byte array
        testWritePartialByteArray()
    }
    
    private fun testWriteSingleByte() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        
        // Write several bytes
        bufferedOutputStream.write(10)
        bufferedOutputStream.write(20)
        bufferedOutputStream.write(30)
        
        // At this point, the bytes should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Flush should write the buffered data to the underlying stream
        bufferedOutputStream.flush()
        assertEquals(3, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
        assertEquals(10, bytes[0].toInt() and 0xff)
        assertEquals(20, bytes[1].toInt() and 0xff)
        assertEquals(30, bytes[2].toInt() and 0xff)
    }
    
    private fun testWriteByteArray() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        // Write the byte array
        bufferedOutputStream.write(data, 0, data.size)
        
        // At this point, the bytes should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Flush should write the buffered data to the underlying stream
        bufferedOutputStream.flush()
        assertEquals(5, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(5, bytes.size)
        assertEquals(1, bytes[0].toInt() and 0xff)
        assertEquals(2, bytes[1].toInt() and 0xff)
        assertEquals(3, bytes[2].toInt() and 0xff)
        assertEquals(4, bytes[3].toInt() and 0xff)
        assertEquals(5, bytes[4].toInt() and 0xff)
    }
    
    private fun testWritePartialByteArray() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        // Write part of the byte array
        bufferedOutputStream.write(data, 1, 3) // Write bytes 2, 3, 4
        
        // At this point, the bytes should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Flush should write the buffered data to the underlying stream
        bufferedOutputStream.flush()
        assertEquals(3, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
        assertEquals(2, bytes[0].toInt() and 0xff)
        assertEquals(3, bytes[1].toInt() and 0xff)
        assertEquals(4, bytes[2].toInt() and 0xff)
    }
}