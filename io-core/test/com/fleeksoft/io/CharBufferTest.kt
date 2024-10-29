package com.fleeksoft.io

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CharBufferTest {

    @Test
    fun initTest() {
        val b1 = CharBufferFactory.allocate(10)
        assertEquals(0, b1.position())
        assertEquals(10, b1.limit())
        assertEquals(10, b1.capacity())
        b1.flipExt()
        assertEquals(0, b1.limit())

        val b2 = CharBufferFactory.wrap(CharArray(5), 0, 3)
        assertEquals(0, b2.position())
        assertEquals(3, b2.limit())
        assertEquals(5, b2.capacity())
    }

    // Test buffer initialization
    @Test
    fun testBufferInitialization() {
        val buffer = CharBufferFactory.allocate(10)
        assertEquals(10, buffer.capacity())
        assertEquals(0, buffer.position())
        assertEquals(10, buffer.limit())
    }

    // Test writing characters to the buffer
    @Test
    fun testWriteCharacters() {
        val buffer = CharBufferFactory.allocate(10)
        buffer.put('A')
        buffer.put('B')
        buffer.put('C')

        assertEquals(3, buffer.position())
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(1))
        assertEquals('C', buffer.getChar(2))
    }

    // Test reading characters from the buffer
    @Test
    fun testReadCharacters() {
        val buffer = CharBufferFactory.allocate(10)
        buffer.put('X')
        buffer.put('Y')
        buffer.put('Z')

        buffer.flipExt() // Prepare for reading
        assertEquals('X', buffer.get())
        assertEquals('Y', buffer.get())
        assertEquals('Z', buffer.get())
    }

    // Test buffer overflow
    @Test
    fun testBufferOverflow() {
        val buffer = CharBufferFactory.allocate(2)
        buffer.put('A')
        buffer.put('B')

        assertFailsWith<BufferOverflowException> {
            buffer.put('C') // This should fail
        }
    }

    // Test buffer underflow
    @Test
    fun testBufferUnderflow() {
        val buffer = CharBufferFactory.allocate(2)
        buffer.put('A')
        buffer.flipExt() // Prepare for reading
        buffer.get()  // Read once

        assertFailsWith<BufferUnderflowException> {
            buffer.get() // This should fail
        }
    }

    // Test clear method
    @Test
    fun testClear() {
        val buffer = CharBufferFactory.allocate(5)
        buffer.put('A')
        buffer.clearExt()

        assertEquals(0, buffer.position())
        assertEquals(buffer.limit(), buffer.capacity())
    }

    // Test marking and resetting the buffer
    @Test
    fun testMarkAndReset() {
        val buffer = CharBufferFactory.allocate(5)
        buffer.put('A')
        buffer.put('B')
        buffer.put('C')

        buffer.flipExt()
        buffer.markExt() // Mark the current position (0)
        buffer.get()  // Read 'A'
        assertEquals('B', buffer.get())

        buffer.resetExt() // Reset to the marked position
        assertEquals('A', buffer.get()) // Should read 'A' again
    }

    // Test buffer position manipulation
    @Test
    fun testPositionManipulation() {
        val buffer = CharBufferFactory.allocate(10)
        buffer.put('A')
        buffer.put('B')
        buffer.setPositionExt(0) // Reset position
        assertEquals('A', buffer.get()) // Should read 'A'
        buffer.setPositionExt(1)
        assertEquals('B', buffer.get()) // Should read 'B'
    }

    // Test limit and capacity
    @Test
    fun testLimitAndCapacity() {
        val buffer = CharBufferFactory.allocate(5)
        assertEquals(5, buffer.capacity())
        buffer.setLimitExt(3)
        assertEquals(3, buffer.limit())

        buffer.put('A')
        buffer.put('B')
        buffer.put('C')

        buffer.flipExt()
        assertEquals('A', buffer.get())
        assertEquals('B', buffer.get())
        assertEquals('C', buffer.get())

        assertFailsWith<BufferUnderflowException> {
            buffer.get() // Should fail as limit is reached
        }
    }
}