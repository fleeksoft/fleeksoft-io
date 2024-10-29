package com.fleeksoft.io.inputstream

import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.EOFException
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.internal.assert
import kotlin.math.min

object Skip {
    private val EOF = -1

    @Throws(Exception::class)
    private fun dotest(
        `in`: InputStream, curpos: Int, total: Long,
        toskip: Long, expected: Long
    ) {
        try {
            println(
                "\n\nCurrently at pos = " + curpos +
                        "\nTotal bytes in the Stream = " + total +
                        "\nNumber of bytes to skip = " + toskip +
                        "\nNumber of bytes that should be skipped = " +
                        expected
            )

            val skipped: Long = `in`.skip(toskip)

            println("actual number skipped: $skipped")

            if ((skipped < 0) || (skipped > expected)) {
                throw RuntimeException("Unexpected byte count skipped")
            }
        } catch (e: IOException) {
            println("IOException is thrown: $e")
        } catch (e: Throwable) {
            throw RuntimeException("Unexpected $e is thrown!")
        }
    }

    private fun dotestExact(
        `in`: MyInputStream, curpos: Long, total: Long,
        toskip: Long, expectIOE: Boolean, expectEOFE: Boolean
    ) {
        println(
            "\n\nCurrently at pos = " + curpos +
                    "\nTotal bytes in the Stream = " + total +
                    "\nNumber of bytes to skip = " + toskip
        )

        try {
            val pos = `in`.position()
            assert(pos == curpos) { "$pos != $curpos" }
            `in`.skipNBytes(toskip)
            if (`in`.position() != pos + (if (toskip < 0) 0 else toskip)) {
                throw RuntimeException(
                    (`in`.position() - pos).toString() +
                            " bytes skipped; expected " + toskip
                )
            }
        } catch (eofe: EOFException) {
            if (!expectEOFE) {
                throw RuntimeException("Unexpected EOFException", eofe)
            }
            println("Caught expected EOFException")
        } catch (ioe: IOException) {
            if (!expectIOE) {
                throw RuntimeException("Unexpected IOException", ioe)
            }
            println("Caught expected IOException")
        }
    }

    @Throws(Exception::class)
    fun main(argv: Array<String?>?) {
        var `in` = MyInputStream(11)

        // test for negative skip
        dotest(`in`, 0, 11, -23, 0)

        // check for skip beyond EOF starting from before EOF
        dotest(`in`, 0, 11, 20, 11)

        // check for skip after EOF
        dotest(`in`, EOF, 11, 20, 0)

        `in` = MyInputStream(9000)

        // check for skip equal to the read chunk size in InputStream.java
        dotest(`in`, 0, 9000, 2048, 2048)

        // check for skip larger than the read chunk size in InputStream.java
        dotest(`in`, 2048, 9000, 5000, 5000)

        // check for skip beyond EOF starting from before EOF
        dotest(`in`, 7048, 9000, 5000, 1952)

        `in` = MyInputStream(5000)

        // check for multiple chunk reads
        dotest(`in`, 0, 5000, 6000, 5000)

        /*
         * check for skip larger than Int.MAX_VALUE
         * (Takes about 2 hrs on a sparc ultra-1)
         * long total = (long)Int.MAX_VALUE + (long)10;
         * long toskip = total - (long)6;
         * in = new MyInputStream(total);
         * dotest(in, 0, total, toskip, toskip);
         */

        // tests for skipping an exact number of bytes
        val streamLength = Long.MAX_VALUE
        `in` = MyInputStream(streamLength)

        // negative skip: OK
        dotestExact(`in`, 0, streamLength, -1, false, false)

        // negative skip at EOF: OK
        `in`.position(streamLength)
        dotestExact(`in`, streamLength, streamLength, -1, false, false)
        `in`.position(0)

        // zero skip: OK
        dotestExact(`in`, 0, streamLength, 0, false, false)

        // zero skip at EOF: OK
        `in`.position(streamLength)
        dotestExact(`in`, streamLength, streamLength, 0, false, false)

        // skip(1) at EOF: EOFE
        dotestExact(`in`, streamLength, streamLength, 1, false, true)
        `in`.position(0)

        val n: Long = 31 // skip count
        var pos: Long = 0

        // skip(n) returns negative value: IOE
        `in`.setState(-1, 100)
        dotestExact(`in`, pos, streamLength, n, true, false)

        // skip(n) returns n + 1: IOE
        `in`.setState(n + 1, 100)
        dotestExact(`in`, pos, streamLength, n, true, false)
        pos += n + 1

        // skip(n) returns n/2 but only n/4 subsequent reads succeed: EOFE
        `in`.setState(n / 2, n / 2 + n / 4)
        dotestExact(`in`, pos, streamLength, n, false, true)
        pos += n / 2 + n / 4

        // skip(n) returns n/2 but n - n/2 subsequent reads succeed: OK
        `in`.setState(n / 2, n)
        dotestExact(`in`, pos, streamLength, n, false, false)
        pos += n
    }

    private class MyInputStream(private val endoffile: Long) : InputStream() {
        private var readctr: Long = 0

        private var isStateSet = false
        private var skipReturn: Long = 0
        private var readLimit: Long = 0

        /**
         * Limits the behavior of skip() and read().
         *
         * @param skipReturn the value to be returned by skip()
         * @param maxReads   the maximum number of reads past the current position
         * before EOF is reached
         */
        fun setState(skipReturn: Long, maxReads: Long) {
            this.skipReturn = skipReturn
            this.readLimit = readctr + maxReads
            isStateSet = true
        }

        override fun read(): Int {
            if (readctr == endoffile ||
                (isStateSet && readctr >= readLimit)
            ) {
                return EOF
            } else {
                readctr++
                return 0
            }
        }

        override fun available(): Int {
            return 0
        }

        fun position(): Long {
            return readctr
        }

        fun position(pos: Long) {
            readctr = if (pos < 0) 0 else min(pos, endoffile)
        }

        override fun skip(n: Long): Long {
            if (isStateSet) {
                return if (skipReturn < 0) skipReturn else super.skip(skipReturn)
            }

            // InputStream skip implementation.
            return super.skip(n) // readctr is implicitly incremented
        }

        companion object {
            private val EOF = -1
        }
    }
}
