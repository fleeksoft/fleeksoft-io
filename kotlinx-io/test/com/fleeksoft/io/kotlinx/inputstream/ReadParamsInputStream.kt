package com.fleeksoft.io.kotlinx.inputstream

import com.fleeksoft.charset.Platform
import com.fleeksoft.charset.isJsOrWasm
import com.fleeksoft.charset.isJvmOrAndroid
import com.fleeksoft.io.BufferedInputStream
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.kotlinx.asInputStream
import com.fleeksoft.io.exception.OutOfMemoryError
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ReadParamsInputStream {
    val fn = Path("x.ReadBounds")

    @BeforeTest
    fun init() {
        if (Platform.isJsOrWasm()) {
//            file read / write issue
            return
        }
        /* initialise stuff */
        SystemFileSystem.sink(fn).buffered().use { sink ->
            for (i in 0..31) {
                sink.writeInt(i)
            }
        }
    }

    @AfterTest
    fun cleanup() {
        if (Platform.isJsOrWasm()) {
//            file read/write issue
            return
        }
        /* cleanup */
        SystemFileSystem.delete(fn)
    }

    @Test
    fun main() {
        if (Platform.isJsOrWasm()) {
//            file read/write issue
            return
        }
        val b = ByteArray(64)
        for (i in 0..63) {
            b[i] = 1
        }


        /* test all input streams */
        val fis = SystemFileSystem.source(fn).buffered().asInputStream()
        doTest(fis)
        fis.close()

        val bis =
            BufferedInputStream(MyInputStream(1024))
        doTest(bis)
        bis.close()

        val bais = ByteArrayInputStream(b)
        doTest(bais)
        bais.close()
    }

    /* check for correct handling of different values of off and len */
    fun doTest(input: InputStream) {
        val off: IntArray = intArrayOf(
            -1, -1, 0, 0, 33, 33, 0, 32, 32, 4, 1, 0, -1,
            Int.MAX_VALUE, 1, Int.MIN_VALUE,
            Int.MIN_VALUE, 1
        )
        val len: IntArray = intArrayOf(
            -1, 0, -1, 33, 0, 4, 32, 0, 4, 16, 31, 0,
            Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE,
            1, -1, Int.MIN_VALUE
        )
        val results: BooleanArray = booleanArrayOf(
            false, false, false, false, false, false,
            true, true, false, true, true, true, false,
            false, false, false, false, false
        )
        val numCases = off.size
        val b = ByteArray(32)
        var numBad = 0

        for (i in 0..<numCases) {
            try {
                input.read(b, off[i], len[i])
            } catch (aiobe: IndexOutOfBoundsException) {
                if (results[i]) {
                    println(
                        "Error:IndexOutOfBoundsException thrown" +
                                " for read(b, " + off[i] + " " + len[i] +
                                " ) on " + input + "\nb.length = 32"
                    )
                    numBad++
                } else {
                    /* println("PassE: " + off[i] + " " + len[i]); */
                }
                continue
            } catch (ome: IllegalArgumentException) {
//                for kotlinx source
                if (results[i]) {
                    println(
                        "Error:IllegalArgumentException thrown" +
                                " for read(b, " + off[i] + " " + len[i] +
                                " ) on " + input + "\nb.length = 32"
                    )
                    numBad++
                } else {
                    /* println("PassE: " + off[i] + " " + len[i]); */
                }
                continue
            } catch (ome: OutOfMemoryError) {
                println(
                    "Error: OutOfMemoryError in read(b, " +
                            off[i] + " " + len[i] + " ) on " + input +
                            "\nb.length = 32"
                )
                numBad++
                continue
            }
            if (!results[i]) {
                println(
                    "Error:No IndexOutOfBoundsException thrown" +
                            " for read(b, " + off[i] + " " + len[i] +
                            " ) on " + input + "\nb.length = 32"
                )
                numBad++
            } else {
                /* println("Pass: " + off[i] + " " + len[i]); */
            }
        }

        if (numBad > 0) {
            throw RuntimeException("$input Failed $numBad cases")
        } else {
            println("Successfully completed bounds tests on $input")
        }
    }

    /* An InputStream class used in the above tests */
    private class MyInputStream(private val endoffile: Long) : InputStream() {
        private var readctr = 0

        override fun read(): Int {
            if (readctr.toLong() == endoffile) {
                return -1
            } else {
                readctr++
                return 0
            }
        }

        override fun available(): Int {
            return 0
        }
    }
}
