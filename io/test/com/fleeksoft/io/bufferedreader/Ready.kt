package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.Reader
import kotlin.test.Test

class Ready {

    @Test
    fun main() {
        var reader: BufferedReader?
        val strings = arrayOf<String?>(
            "LF-Only\n",
            "LF-Only\n",
            "CR/LF\r\n",
            "CR/LF\r\n",
            "CR-Only\r",
            "CR-Only\r",
            "CR/LF line\r\nMore data.\r\n",
            "CR/LF line\r\nMore data.\r\n"
        )

        // The buffer sizes are chosen such that the boundary conditions are
        // tested.
        val bufsizes = intArrayOf(7, 8, 6, 5, 7, 8, 11, 10)

        for (i in strings.indices) {
            reader = BufferedReader(
                Ready.BoundedReader(strings[i]!!),
                bufsizes[i]
            )
            while (reader.ready()) {
                val str: String? = reader.readLine()
                println("read>>$str")
            }
        }
    }


    private class BoundedReader(content: String) : Reader() {
        private val content: CharArray
        private val limit: Int = content.length
        private var pos = 0

        init {
            this.content = CharArray(limit)
            content.toCharArray(0, limit).copyInto(this.content)
        }

        override fun read(): Int {
            if (pos >= limit) throw RuntimeException("Hit infinite wait condition")
            return content[pos++].code
        }

        override fun read(buf: CharArray, off: Int, len: Int): Int {
            if (pos >= limit) throw RuntimeException("Hit infinite wait condition")
            val oldPos = pos
            val readlen = if (len > (limit - pos)) (limit - pos) else len
            for (i in off..<readlen) {
                buf[i] = read().toChar()
            }

            return (pos - oldPos)
        }

        override fun close() {}

        override fun ready(): Boolean {
            return pos < limit
        }
    }
}
