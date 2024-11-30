package com.fleeksoft.io.reader

import com.fleeksoft.io.CharArrayReader
import com.fleeksoft.io.Reader
import com.fleeksoft.io.StringReader
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * @test
 * @bug 8248383
 * @summary Ensure that zero is returned for read into zero length array
 * @run testng ReadIntoZeroLengthArray
 */
class ReadIntoZeroLengthArray {
//    private var file: File? = null

    private var cbuf0: CharArray = CharArray(0)
    private var cbuf1: CharArray = CharArray(1)

    @BeforeTest
    fun setup() {
//        file = File.createTempFile("foo", "bar", File("."))
        cbuf0 = CharArray(0)
        cbuf1 = CharArray(1)
    }

    @AfterTest
    fun teardown() {
//        file.delete()
    }

    @Test
    fun main() {
        readersArray.forEach { readers ->
            readers.forEach { reader ->
                test0(reader)
            }
        }
        readersArray.forEach { readers ->
            readers.forEach { reader ->
                test1(reader)
            }
        }
    }

    val readersArray: Array<Array<Reader>>
        get() {
//            val fileReader: Reader = FileReader(file)
            return arrayOf(
                arrayOf(CharArrayReader(charArrayOf(27.toChar()))),
//                arrayOf(PushbackReader(fileReader)),
//                arrayOf(fileReader),
                arrayOf(StringReader(byteArrayOf(42.toByte()).decodeToString()))
            )
        }

    fun test0(r: Reader) {
        assertEquals(r.read(cbuf0), 0)
    }

    fun test1(r: Reader) {
        assertEquals(r.read(cbuf1, 0, 0), 0)
    }
}
