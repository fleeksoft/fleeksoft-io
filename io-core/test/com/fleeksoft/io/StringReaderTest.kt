package com.fleeksoft.io

import com.fleeksoft.io.exception.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StringReaderTest {

    @Test
    fun testReadSingleChar() {
        val reader = StringReader("Hello")
        
        assertEquals('H'.code, reader.read())
        assertEquals('e'.code, reader.read())
        assertEquals('l'.code, reader.read())
        assertEquals('l'.code, reader.read())
        assertEquals('o'.code, reader.read())
        assertEquals(-1, reader.read()) // End of stream
    }

    @Test
    fun testReadCharArray() {
        val reader = StringReader("Hello, World!")
        val buffer = CharArray(5)
        
        val charsRead = reader.read(buffer, 0, 5)
        
        assertEquals(5, charsRead)
        assertEquals('H', buffer[0])
        assertEquals('e', buffer[1])
        assertEquals('l', buffer[2])
        assertEquals('l', buffer[3])
        assertEquals('o', buffer[4])
        
        // Read more characters
        val buffer2 = CharArray(7)
        val charsRead2 = reader.read(buffer2, 0, 7)
        
        assertEquals(7, charsRead2)
        assertEquals(',', buffer2[0])
        assertEquals(' ', buffer2[1])
        assertEquals('W', buffer2[2])
        assertEquals('o', buffer2[3])
        assertEquals('r', buffer2[4])
        assertEquals('l', buffer2[5])
        assertEquals('d', buffer2[6])
        
        // Read remaining characters
        val buffer3 = CharArray(1)
        val charsRead3 = reader.read(buffer3, 0, 1)
        
        assertEquals(1, charsRead3)
        assertEquals('!', buffer3[0])
        
        // Try to read more (should return -1)
        val buffer4 = CharArray(1)
        val charsRead4 = reader.read(buffer4, 0, 1)
        
        assertEquals(-1, charsRead4)
    }

    @Test
    fun testReadPartialCharArray() {
        val reader = StringReader("Hello")
        val buffer = CharArray(10)
        
        // Read with offset
        val charsRead = reader.read(buffer, 2, 3)
        
        assertEquals(3, charsRead)
        assertEquals('H', buffer[2])
        assertEquals('e', buffer[3])
        assertEquals('l', buffer[4])
        
        // Read remaining characters
        val charsRead2 = reader.read(buffer, 5, 2)
        
        assertEquals(2, charsRead2)
        assertEquals('l', buffer[5])
        assertEquals('o', buffer[6])
    }

    @Test
    fun testReadEmptyString() {
        val reader = StringReader("")
        
        assertEquals(-1, reader.read())
        
        val buffer = CharArray(5)
        val charsRead = reader.read(buffer, 0, 5)
        
        assertEquals(-1, charsRead)
    }

    @Test
    fun testSkip() {
        val reader = StringReader("Hello, World!")
        
        // Skip 7 characters
        val skipped = reader.skip(7)
        
        assertEquals(7, skipped)
        assertEquals('W'.code, reader.read())
        
        // Skip 2 more characters
        val skipped2 = reader.skip(2)
        
        assertEquals(2, skipped2)
        assertEquals('l'.code, reader.read())
        
        // Skip more than available
        val skipped3 = reader.skip(10)
        
        assertEquals(2, skipped3) // Only 2 characters left to skip
        assertEquals(-1, reader.read()) // End of stream
    }

    @Test
    fun testSkipNegative() {
        val reader = StringReader("Hello")
        
        // Read 3 characters
        reader.read() // H
        reader.read() // e
        reader.read() // l
        
        // Skip backwards 2 characters
        val skipped = reader.skip(-2)
        
        assertEquals(-2, skipped)
        assertEquals('e'.code, reader.read())
        
        // Skip backwards more than available
        val skipped2 = reader.skip(-10)
        
        assertEquals(-2, skipped2) // Can only skip back to the beginning
        assertEquals('H'.code, reader.read())
    }

    @Test
    fun testReady() {
        val reader = StringReader("Hello")
        
        assertTrue(reader.ready())
        
        // Read all characters
        reader.read() // H
        reader.read() // e
        reader.read() // l
        reader.read() // l
        reader.read() // o
        
        // Should still be ready even at end of stream
        assertTrue(reader.ready())
    }

    @Test
    fun testMarkAndReset() {
        val reader = StringReader("Hello, World!")
        
        assertTrue(reader.markSupported())
        
        // Read 7 characters
        for (i in 0 until 7) {
            reader.read()
        }
        
        // Mark at position 7 (just before 'W')
        reader.mark(0)
        
        assertEquals('W'.code, reader.read())
        assertEquals('o'.code, reader.read())
        
        // Reset to position 7
        reader.reset()
        
        assertEquals('W'.code, reader.read()) // Should read 'W' again
    }

    @Test
    fun testClose() {
        val reader = StringReader("Hello")
        
        reader.close()
        
        // Trying to read after closing should throw IOException
        assertFailsWith<IOException> {
            reader.read()
        }
        
        // Trying to read into buffer after closing should throw IOException
        assertFailsWith<IOException> {
            val buffer = CharArray(5)
            reader.read(buffer, 0, 5)
        }
        
        // Trying to skip after closing should throw IOException
        assertFailsWith<IOException> {
            reader.skip(1)
        }
        
        // Trying to mark after closing should throw IOException
        assertFailsWith<IOException> {
            reader.mark(0)
        }
        
        // Trying to reset after closing should throw IOException
        assertFailsWith<IOException> {
            reader.reset()
        }
    }

    @Test
    fun testReadZeroLength() {
        val reader = StringReader("Hello")
        val buffer = CharArray(5)
        
        // Reading zero characters should return 0
        val charsRead = reader.read(buffer, 0, 0)
        
        assertEquals(0, charsRead)
    }
}