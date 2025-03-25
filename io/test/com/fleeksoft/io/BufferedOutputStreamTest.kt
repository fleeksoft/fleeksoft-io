package com.fleeksoft.io

import kotlin.test.Test
import kotlin.test.assertEquals

class BufferedOutputStreamTest {

    @Test
    fun testDefaultConstructor() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        
        // Write a single byte
        bufferedOutputStream.write(1)
        
        // At this point, the byte should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Flush should write the buffered data to the underlying stream
        bufferedOutputStream.flush()
        assertEquals(1, outputStream.size())
        assertEquals(1, outputStream.toByteArray()[0].toInt() and 0xff)
    }

    @Test
    fun testConstructorWithSize() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream, 10)
        
        // Write a single byte
        bufferedOutputStream.write(1)
        
        // At this point, the byte should be in the buffer but not yet written to the underlying stream
        assertEquals(0, outputStream.size())
        
        // Flush should write the buffered data to the underlying stream
        bufferedOutputStream.flush()
        assertEquals(1, outputStream.size())
        assertEquals(1, outputStream.toByteArray()[0].toInt() and 0xff)
    }

    @Test
    fun testWriteSingleByte() {
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

    @Test
    fun testWriteByteArray() {
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

    @Test
    fun testWritePartialByteArray() {
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

    @Test
    fun testBufferOverflow() {
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

    @Test
    fun testWriteLargeByteArray() {
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

    @Test
    fun testClose() {
        val outputStream = ByteArrayOutputStream()
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        
        // Write some data
        bufferedOutputStream.write(1)
        bufferedOutputStream.write(2)
        
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