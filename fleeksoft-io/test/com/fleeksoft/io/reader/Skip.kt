package com.fleeksoft.io.reader

import com.fleeksoft.io.CharArrayReader
import com.fleeksoft.io.Reader
import com.fleeksoft.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals

class Skip {

    val readersArray: Array<Array<Reader>>
        get() = arrayOf(
//            arrayOf(LineNumberReader(FileReader(Skip.Companion.file))),
            arrayOf(CharArrayReader(charArrayOf(27.toChar()))),
//            arrayOf(PushbackReader(FileReader(Skip.Companion.file))),
//            arrayOf(FileReader(Skip.Companion.file)),
            arrayOf(StringReader(byteArrayOf(42.toByte()).decodeToString()))
        )

    @Test
    fun testEof() {
        readersArray.forEach { readers ->
            readers.forEach { reader ->
                eof(reader)
            }
        }
    }

    fun eof(r: Reader) {
        r.skip(Long.MAX_VALUE)
        assertEquals(r.skip(1), 0)
        assertEquals(r.read(), -1)
    }


    val skipNoIAEs: Array<Array<Reader>>
        get() = arrayOf(
            arrayOf(CharArrayReader(charArrayOf(27.toChar()))),
            arrayOf(StringReader(byteArrayOf(42.toByte()).decodeToString()))
        )

    @Test
    fun testNoIAE() {
        skipNoIAEs.forEach { readers ->
            readers.forEach { reader ->
                testNoIAEInternal(reader)
            }
        }
    }

    fun testNoIAEInternal(r: Reader) {
        r.skip(-1)
    }
}
