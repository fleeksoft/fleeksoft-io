package com.fleeksoft.io.filteroutputstream

import com.fleeksoft.io.FilterOutputStream
import com.fleeksoft.io.OutputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class FlushTest {

    @Test
    fun main() {
        val mockOutputStream = MockOutputStream()
        val filterOutputStream = FilterOutputStream(mockOutputStream)
        
        // Initially, the mock output stream should not be flushed
        assertTrue(!mockOutputStream.flushed)
        
        // Flush the filter output stream
        filterOutputStream.flush()
        
        // The mock output stream should now be flushed
        assertTrue(mockOutputStream.flushed)
    }
    
    /**
     * A mock OutputStream that tracks whether flush() has been called
     */
    private class MockOutputStream : OutputStream() {
        var flushed = false
        
        override fun write(b: Int) {
            // Do nothing
        }
        
        override fun flush() {
            flushed = true
        }
    }
}