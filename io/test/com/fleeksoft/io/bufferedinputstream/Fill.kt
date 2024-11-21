package com.fleeksoft.io.bufferedinputstream

import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.InputStream
import kotlin.test.Test

class Fill {

    @Test
    fun main() {
        for (i in 0..7) go(i)
    }

    /**
     * Test BufferedInputStream with an underlying source that always reads
     * shortFall fewer bytes than requested
     */
    private fun go(shortFall: Int) {
        val r: InputStream = BufferedInputStream(Source(shortFall), 10)
        val buf = ByteArray(8)

        val n1: Int = r.read(buf)
        val n2: Int = r.read(buf)
        println("Shortfall $shortFall: Read $n1, then $n2 bytes")
        if (n1 != buf.size) throw Exception("First read returned $n1")
        if (n2 != buf.size) throw Exception("Second read returned $n2")
    }

    /**
     * A simple InputStream that is always ready but may read fewer than the
     * requested number of bytes
     */
    internal class Source(var shortFall: Int) : InputStream() {
        var next: Byte = 0

        override fun read(): Int {
            return (next++).toInt()
        }

        override fun read(buf: ByteArray, off: Int, len: Int): Int {
            val n = len - shortFall
            for (i in off..<n) buf[i] = next++
            return n
        }

        override fun available(): Int {
            return Int.MAX_VALUE
        }

        override fun close() {
        }
    }
}
