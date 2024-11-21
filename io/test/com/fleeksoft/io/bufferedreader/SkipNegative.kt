package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.CharArrayReader
import kotlin.test.Test

/* @test
   @bug 4152453
   @summary Skip must throw an exception for negative args
*/
class SkipNegative {

    @Test
    fun main() {
        val cbuf: CharArray = "testString".toCharArray()
        val CAR = CharArrayReader(cbuf)
        val BR = BufferedReader(CAR)
        val nchars = -1L
        try {
            val actual: Long = BR.skip(nchars)
        } catch (e: IllegalArgumentException) {
            // Negative argument caught
            return
        }
        throw Exception("Skip should not accept negative values")
    }
}
