package com.fleeksoft.io.reader

import com.fleeksoft.io.*
import kotlin.test.Test

/*
 * @test
 * @bug 8029689
 * @summary checks the bounds part of the contract of java.io.Reader.read(char[], int, int):
 *
 *              0 <= off <= off+len <= cbuf.length
 *
 *          for publicly exported subtypes of java.io.Reader
 */
class ReaderBulkReadContract {

    @Test
    fun main() {
        val t = ReaderBulkReadContract()
        t.test()
    }

    private fun test() {
        val args: Iterator<Array<Any>> = args()
        while (args.hasNext()) {
            val a: Array<Any> = args.next()
            val r: Reader = a[0] as Reader
            val size = a[1] as Int
            val off = a[2] as Int
            val len = a[3] as Int
            try {
                read(r, size, off, len)
            } finally {
                r.close()
            }
        }
    }

    private fun args(): Iterator<Array<Any>> {
        val lens: Array<Int> = arrayOf<Int>(Int.MIN_VALUE, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, Int.MAX_VALUE)
        val offs: Array<Int> = arrayOf<Int>(Int.MIN_VALUE, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, Int.MAX_VALUE)
        val sizes: Array<Int> = arrayOf<Int>(0, 1, 2, 3, 4, 5)
        val contents = arrayOf<String>("", "a", "ab")

        val fs: List<(String) -> Reader> = listOf(
            { s: String -> BufferedReader(StringReader(s)) },
            { s: String -> CharArrayReader(s.toCharArray()) },
            { s: String -> InputStreamReader(ByteArrayInputStream(s.encodeToByteArray())) },
            { s: String -> PushbackReader(StringReader(s)) },
            { s: String -> StringReader(s) }
        )

        // The easiest way to produce a cartesian product from a small fixed number of sets
        val tuples: MutableList<Array<Any>> = mutableListOf()
        for (len in lens) for (off in offs) for (s in contents) for (size in sizes) for (f in fs) tuples.add(
            arrayOf(
                f.invoke(s),
                size,
                off,
                len
            )
        )

        return tuples.iterator()
    }


    private fun read(r: Reader, size: Int, off: Int, len: Int) {
        var ex: IndexOutOfBoundsException? = null
        try {
            r.read(CharArray(size), off, len)
        } catch (e: IndexOutOfBoundsException) {
            ex = e
        }

        val incorrectBounds = off < 0 || len < 0 || len > size - off
        val exceptionThrown = ex != null

        if (incorrectBounds != exceptionThrown) { // incorrectBounds iff exceptionThrown
            throw AssertionError("r=$r, size=$size, off=$off, len=$len, incorrectBounds=$incorrectBounds, exceptionThrown=$exceptionThrown")
        }
    }
}
