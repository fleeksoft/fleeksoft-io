package com.fleeksoft.io.filteroutputstream

import com.fleeksoft.io.ByteArrayOutputStream
import com.fleeksoft.io.FilterOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteTest {

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
        val filterOutputStream = FilterOutputStream(outputStream)
        
        // Write several bytes
        filterOutputStream.write(10)
        filterOutputStream.write(20)
        filterOutputStream.write(30)
        
        // FilterOutputStream should write directly to the underlying stream
        assertEquals(3, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
        assertEquals(10, bytes[0].toInt() and 0xff)
        assertEquals(20, bytes[1].toInt() and 0xff)
        assertEquals(30, bytes[2].toInt() and 0xff)
    }
    
    private fun testWriteByteArray() {
        val outputStream = ByteArrayOutputStream()
        val filterOutputStream = FilterOutputStream(outputStream)
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        // Write the byte array
        filterOutputStream.write(data)
        
        // FilterOutputStream should write directly to the underlying stream
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
        val filterOutputStream = FilterOutputStream(outputStream)
        val data = byteArrayOf(1, 2, 3, 4, 5)
        
        // Write part of the byte array
        filterOutputStream.write(data, 1, 3) // Write bytes 2, 3, 4
        
        // FilterOutputStream should write directly to the underlying stream
        assertEquals(3, outputStream.size())
        
        val bytes = outputStream.toByteArray()
        assertEquals(3, bytes.size)
        assertEquals(2, bytes[0].toInt() and 0xff)
        assertEquals(3, bytes[1].toInt() and 0xff)
        assertEquals(4, bytes[2].toInt() and 0xff)
    }
}