package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.StringReader
import kotlin.test.Test

/* @test
   @bug 4069687
   @summary Test if fill() will behave correctly at EOF
            when mark is set.
*/
class MarkedFillAtEOF {
    @Throws(Exception::class)
    @Test
    fun main() {
        val r: BufferedReader = BufferedReader(StringReader("12"))
        var count = 0

        r.read()
        r.mark(2)
        // trigger the call to fill()
        while (r.read() !== -1);
        r.reset()

        // now should only read 1 character
        while (r.read() !== -1) {
            count++
        }
        if (count != 1) {
            throw Exception("Expect 1 character, but got " + count)
        }
    }
}
