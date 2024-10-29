package com.fleeksoft.io.inputstreamreader

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.InputStreamReader
import kotlin.test.Test

/* @test
 * @bug 4399447
 * @summary Ensure that read offsets work properly
 */
class ReadOffset {
    @Test
    fun testReadOffset() {
        val `is`: InputStream = ByteArrayInputStream("foo bar".toByteArray(Charsets.forName("US-ASCII")))
        val isr = InputStreamReader(`is`, "US-ASCII")
        val cbuf = CharArray(100)
        val n: Int
        println(isr.read(cbuf, 0, 3).also { n = it })
        println(isr.read(cbuf, n, cbuf.size - n))
    }
}
