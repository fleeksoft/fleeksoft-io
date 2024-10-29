package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.StringReader
import kotlin.test.Test

/* @test
   @bug 4072575
   @summary Test all the EOL delimiters accepted by BufferedReader
*/
class EOL {
    @Test
    fun main() {
        val sr = StringReader("one\rtwo\r\nthree\nfour\r")
        val br: BufferedReader = BufferedReader(sr)
        var i = 0
        while (true) {
            val l: String? = br.readLine()
            if (l == null) {
                if (i != 4) throw RuntimeException("Expected 4 lines, got $i")
                break
            }
            println("$i: $l")
            i++
        }
    }
}
