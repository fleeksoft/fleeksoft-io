package com.fleeksoft.io

import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayOutputStreamTest {

    @Test
    fun testDefaultConstructor() {
        val outputStream = ByteArrayOutputStream()
        assertEquals(0, outputStream.size())
        
        // Write some data to ensure it works
        outputStream.write(1)
        assertEquals(1, outputStream.size())
    }

    @Test
    fun testConstructorWithSize() {
        val outputStream = ByteArrayOutputStream(10)
        assertEquals(0, outputStream.size())
        
        // Write some data to ensure it works
        outputStream.write(1)
        assertEquals(1, outputStream.size())
    }

    @Test
    fun testWriteSingleByte() {
        val outputStream = ByteArrayOutputStream()
        
        outputStream.write(10)
        outputStream.write(20)
        outputStream.write(30)
        
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
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        outputStream.write(data, 0, data.size)
        
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
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        outputStream.write(data, 1, 3) // Write bytes 2, 3, 4
        
        assertEquals(3, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
        assertEquals(2, bytes[0].toInt() and 0xff)
        assertEquals(3, bytes[1].toInt() and 0xff)
        assertEquals(4, bytes[2].toInt() and 0xff)
    }

    @Test
    fun testWriteBytes() {
        val outputStream = ByteArrayOutputStream()
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        outputStream.writeBytes(data)
        
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
    fun testReset() {
        val outputStream = ByteArrayOutputStream()
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        outputStream.writeBytes(data)
        assertEquals(5, outputStream.size())
        
        outputStream.reset()
        assertEquals(0, outputStream.size())
        
        // Write new data after reset
        outputStream.write(10)
        assertEquals(1, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(1, bytes.size)
        assertEquals(10, bytes[0].toInt() and 0xff)
    }

    @Test
    fun testToString() {
        val outputStream = ByteArrayOutputStream()
        
        // Write ASCII characters
        outputStream.write('H'.code)
        outputStream.write('e'.code)
        outputStream.write('l'.code)
        outputStream.write('l'.code)
        outputStream.write('o'.code)
        
        val str = outputStream.toString()
        assertEquals("Hello", str)
    }

    @Test
    fun testWriteTo() {
        val outputStream1 = ByteArrayOutputStream()
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        outputStream1.writeBytes(data)
        
        val outputStream2 = ByteArrayOutputStream()
        outputStream1.writeTo(outputStream2)
        
        assertEquals(5, outputStream2.size())
        
        val bytes = outputStream2.toByteArray()
        assertEquals(5, bytes.size)
        assertEquals(1, bytes[0].toInt() and 0xff)
        assertEquals(2, bytes[1].toInt() and 0xff)
        assertEquals(3, bytes[2].toInt() and 0xff)
        assertEquals(4, bytes[3].toInt() and 0xff)
        assertEquals(5, bytes[4].toInt() and 0xff)
    }

    @Test
    fun testCapacityExpansion() {
        // Create a small output stream
        val outputStream = ByteArrayOutputStream(2)
        
        // Write more bytes than the initial capacity
        for (i in 1..10) {
            outputStream.write(i)
        }
        
        assertEquals(10, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(10, bytes.size)
        for (i in 1..10) {
            assertEquals(i, bytes[i-1].toInt() and 0xff)
        }
    }

    @Test
    fun testClose() {
        val outputStream = ByteArrayOutputStream()
        val data = byteArrayOf(1, 2, 3)
        
        outputStream.writeBytes(data)
        outputStream.close()
        
        // Should still be able to access the data after closing
        assertEquals(3, outputStream.size())
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
    }
}