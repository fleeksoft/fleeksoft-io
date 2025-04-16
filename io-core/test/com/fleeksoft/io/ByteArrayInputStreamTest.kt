package com.fleeksoft.io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ByteArrayInputStreamTest {

    @Test
    fun testConstructorWithByteArray() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        assertEquals(5, inputStream.available())
        assertEquals(1, inputStream.read())
        assertEquals(4, inputStream.available())
    }

    @Test
    fun testConstructorWithOffsetAndLength() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data, 1, 3)
        
        assertEquals(3, inputStream.available())
        assertEquals(2, inputStream.read())
        assertEquals(2, inputStream.available())
    }

    @Test
    fun testReadSingleByte() {
        val data = byteArrayOf(10, 20, 30)
        val inputStream = ByteArrayInputStream(data)
        
        assertEquals(10, inputStream.read())
        assertEquals(20, inputStream.read())
        assertEquals(30, inputStream.read())
        assertEquals(-1, inputStream.read()) // End of stream
    }

    @Test
    fun testReadByteArray() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        val buffer = ByteArray(3)
        val bytesRead = inputStream.read(buffer, 0, 3)
        
        assertEquals(3, bytesRead)
        assertEquals(1, buffer[0])
        assertEquals(2, buffer[1])
        assertEquals(3, buffer[2])
        
        // Read remaining bytes
        val buffer2 = ByteArray(2)
        val bytesRead2 = inputStream.read(buffer2, 0, 2)
        
        assertEquals(2, bytesRead2)
        assertEquals(4, buffer2[0])
        assertEquals(5, buffer2[1])
        
        // Try to read more (should return -1)
        val buffer3 = ByteArray(1)
        val bytesRead3 = inputStream.read(buffer3, 0, 1)
        
        assertEquals(-1, bytesRead3)
    }

    @Test
    fun testReadAllBytes() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        // Read a byte first to advance the position
        inputStream.read()
        
        val allBytes = inputStream.readAllBytes()
        
        assertEquals(4, allBytes.size)
        assertEquals(2, allBytes[0])
        assertEquals(3, allBytes[1])
        assertEquals(4, allBytes[2])
        assertEquals(5, allBytes[3])
        
        // Stream should be at the end
        assertEquals(0, inputStream.available())
    }

    @Test
    fun testReadNBytes() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        val buffer = ByteArray(3)
        val bytesRead = inputStream.readNBytes(buffer, 0, 3)
        
        assertEquals(3, bytesRead)
        assertEquals(1, buffer[0])
        assertEquals(2, buffer[1])
        assertEquals(3, buffer[2])
        
        // Try to read more than available
        val buffer2 = ByteArray(3)
        val bytesRead2 = inputStream.readNBytes(buffer2, 0, 3)
        
        assertEquals(2, bytesRead2) // Only 2 bytes left
        assertEquals(4, buffer2[0])
        assertEquals(5, buffer2[1])
        
        // Try to read when stream is empty
        val buffer3 = ByteArray(1)
        val bytesRead3 = inputStream.readNBytes(buffer3, 0, 1)
        
        assertEquals(0, bytesRead3) // No bytes available
    }

    @Test
    fun testSkip() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        val skipped = inputStream.skip(2)
        
        assertEquals(2, skipped)
        assertEquals(3, inputStream.read())
        
        // Skip more than available
        val skipped2 = inputStream.skip(10)
        
        assertEquals(2, skipped2) // Only 2 bytes left to skip
        assertEquals(0, inputStream.available())
    }

    @Test
    fun testMarkAndReset() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        assertTrue(inputStream.markSupported())
        
        inputStream.read() // Read first byte
        inputStream.mark(0) // Mark at position 1
        
        assertEquals(2, inputStream.read())
        assertEquals(3, inputStream.read())
        
        inputStream.reset() // Reset to position 1
        
        assertEquals(2, inputStream.read()) // Should read the second byte again
    }

    @Test
    fun testTransferTo() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        val outputStream = ByteArrayOutputStream()
        
        val transferred = inputStream.transferTo(outputStream)
        
        assertEquals(5, transferred)
        assertEquals(0, inputStream.available())
        
        val outputBytes = outputStream.toByteArray()
        assertEquals(5, outputBytes.size)
        assertEquals(1, outputBytes[0])
        assertEquals(2, outputBytes[1])
        assertEquals(3, outputBytes[2])
        assertEquals(4, outputBytes[3])
        assertEquals(5, outputBytes[4])
    }

    @Test
    fun testPartialTransferTo() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val inputStream = ByteArrayInputStream(data)
        
        // Read first byte
        inputStream.read()
        
        val outputStream = ByteArrayOutputStream()
        val transferred = inputStream.transferTo(outputStream)
        
        assertEquals(4, transferred)
        assertEquals(0, inputStream.available())
        
        val outputBytes = outputStream.toByteArray()
        assertEquals(4, outputBytes.size)
        assertEquals(2, outputBytes[0])
        assertEquals(3, outputBytes[1])
        assertEquals(4, outputBytes[2])
        assertEquals(5, outputBytes[3])
    }
}