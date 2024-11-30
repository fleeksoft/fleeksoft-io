package com.fleeksoft.io.bufferedinputstream

import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.IOException
import kotlin.test.Test

/**
 * This class tests to see if bufferinputstream updates count
 * when the stream is interrupted and restarted
 * It was adapted from a test class provided in the bug report
 *
 */
class CountUpdate {

    @Test
    fun main() {
        val breaker = BufferBreaker()
        val `in` = BufferedInputStream(breaker, 1000)

        val b = ByteArray(100)
        var total = 0

        for (i in 0..4) {
            if (i > 0) breaker.breakIt = true
            try {
                val n: Int = `in`.read(b)
                total += n
                //System.out.print("read "+n+" bytes: [");
                //System.out.write(b, 0, n);
                //println("]");
            } catch (e: IOException) {
                //println(e);
            }
        }

        if (total > 7) throw RuntimeException(
            "BufferedInputStream did not reset count."
        )
    }
}

private class BufferBreaker : InputStream() {
    var breakIt: Boolean = false

    override fun read(): Int {
        return 'x'.code
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var len = len
        if (breakIt) throw IOException("BREAK")
        if (len > buffer.size) len = buffer.size
        buffer.copyInto(b, off, 0, len)
        return len
    }

    override fun skip(n: Long): Long {
        return 0
    }

    override fun available(): Int {
        return 0
    }

    companion object {
        val buffer: ByteArray = byteArrayOf(
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte(),
            'g'.code.toByte()
        )
    }
}
