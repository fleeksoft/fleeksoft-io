package com.fleeksoft.io.bufferedreader

import com.fleeksoft.io.BufferedReader
import com.fleeksoft.io.Reader
import kotlin.test.Test

/* @test
 * @bug 4151072
 * @summary Ensure that BufferedReader's methods handle the new line character
 *          following the carriage return correctly after a readLine
 *          operation that resulted in reading a line terminated by a
 *          carriage return (\r).
 */
class ReadLine {

    @Test
    fun main() {
        // Make sure that the reader does not wait for additional characters to
        // be read after reading a new line.
        var reader: BufferedReader?
        val strings = arrayOf<Array<String?>?>(
            arrayOf<String?>("CR/LF\r\n", "CR/LF"),
            arrayOf<String?>("LF-Only\n", "LF-Only"),
            arrayOf<String?>("CR-Only\r", "CR-Only"),
            arrayOf<String?>("CR/LF line\r\nMore data", "More data")
        )

        // test 0 "CR/LF\r\n"
        // test 1 "LF-Only\n"
        // test 2 "CR-Only\r"
        for (i in 0..2) {
            reader = BufferedReader(ReadLine.BoundedReader(strings[i]!![0]!!), strings[i]!![0]!!.length)
            if (!reader.readLine().equals(strings[i]!![1])) throw RuntimeException("Read incorrect text")
        }


        // Now test the mark and reset operations. Consider two cases.
        // 1. For lines ending with CR only.
        markResetTest(
            "Lot of textual data\rMore textual data\n",
            "More textual data"
        )

        // 2. Now for lines ending with CR/LF
        markResetTest(
            "Lot of textual data\r\nMore textual data\n",
            "More textual data"
        )

        // 3. Now for lines ending with LF only
        markResetTest(
            "Lot of textual data\nMore textual data\n",
            "More textual data"
        )

        // Need to ensure behavior of read() after a readLine() read of a CR/LF
        // terminated line.
        // 1.  For lines ending with CR/LF only.

        // uses "CR/LF line\r\nMore data"
        reader = BufferedReader(ReadLine.BoundedReader(strings[3]!![0]!!), strings[3]!![0]!!.length)
        reader.readLine()
        if (reader.read().toChar() != 'M') {
            throw RuntimeException("Read() failed")
        }


        // Need to ensure that a read(char[], int, int) following a readLine()
        // read of a CR/LF terminated line behaves correctly.

        // uses "CR/LF line\r\nMore data"
        reader = BufferedReader(ReadLine.BoundedReader(strings[3]!![0]!!), strings[3]!![0]!!.length)
        reader.readLine()

        val buf = CharArray(9)
        reader.read(buf, 0, 9)
        val newStr = buf.concatToString()
        if (newStr != strings[3]!![1]) throw RuntimeException("Read(char[],int,int) failed")
    }


    fun markResetTest(inputStr: String, resetStr: String?) {
        val reader = BufferedReader(BoundedReader(inputStr), inputStr.length)
        println("> " + reader.readLine())
        reader.mark(30)
        println("......Marking stream .....")
        val str: String? = reader.readLine()
        println("> $str")
        reader.reset()
        val newStr: String? = reader.readLine()
        println("reset> $newStr")

        // Make sure that the reset point was set correctly.
        if (!newStr.equals(resetStr)) throw RuntimeException("Mark/Reset failed")
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
            if (pos >= limit) throw RuntimeException("Read past limit")
            return content[pos++].code
        }

        override fun read(buf: CharArray, off: Int, len: Int): Int {
            val oldPos = pos
            for (i in off..<len) {
                buf[i] = read().toChar()
            }
            return (pos - oldPos)
        }

        override fun close() {}
    }
}
