package com.fleeksoft.io.filteroutputstream

import com.fleeksoft.io.FilterOutputStream
import com.fleeksoft.io.OutputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class CloseTest {

    @Test
    fun main() {
        val mockOutputStream = MockOutputStream()
        val filterOutputStream = FilterOutputStream(mockOutputStream)
        
        // Initially, the mock output stream should not be flushed or closed
        assertTrue(!mockOutputStream.flushed)
        assertTrue(!mockOutputStream.closed)
        
        // Close the filter output stream
        filterOutputStream.close()
        
        // The mock output stream should now be flushed and closed
        assertTrue(mockOutputStream.flushed)
        assertTrue(mockOutputStream.closed)
        
        // Closing again should be a no-op
        mockOutputStream.flushed = false
        mockOutputStream.closed = false
        
        filterOutputStream.close()
        
        // The mock output stream should not be flushed or closed again
        assertTrue(!mockOutputStream.flushed)
        assertTrue(!mockOutputStream.closed)
    }
    
    /**
     * A mock OutputStream that tracks whether flush() and close() have been called
     */
    private class MockOutputStream : OutputStream() {
        var flushed = false
        var closed = false
        
        override fun write(b: Int) {
            // Do nothing
        }
        
        override fun flush() {
            flushed = true
        }
        
        override fun close() {
            closed = true
        }
    }
}