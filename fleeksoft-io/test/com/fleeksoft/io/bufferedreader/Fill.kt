package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.Reader
import kotlin.test.Test

class Fill {
    /**
     * Test BufferedReader with an underlying source that always reads
     * shortFall fewer characters than requested
     */


    @Test
    fun main() {
        for (i in 0..7) go(i)
    }

    fun go(shortFall: Int) {
        val r = BufferedReader(Source(shortFall), 10)
        val cbuf = CharArray(8)

        val n1: Int = r.read(cbuf)
        val n2: Int = r.read(cbuf)
        println("Shortfall $shortFall: Read $n1, then $n2 chars")
        if (n1 != cbuf.size) throw Exception("First read returned $n1")
        if (n2 != cbuf.size) throw Exception("Second read returned $n2")
    }

    /**
     * A simple Reader that is always ready but may read fewer than the
     * requested number of characters
     */
    internal class Source(var shortFall: Int) : Reader() {
        var next: Char = 0.toChar()

        override fun read(cbuf: CharArray, off: Int, len: Int): Int {
            val n = len - shortFall
            for (i in off..<n) cbuf[i] = next++
            return n
        }

        override fun ready(): Boolean {
            return true
        }

        override fun close() {
        }
    }
}
