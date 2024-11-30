package com.fleeksoft.charset

import com.fleeksoft.io.ByteBufferFactory
import com.fleeksoft.io.CharBufferFactory
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.test.Test

class CharsetJvmTest {

    @Test
    fun test() {
        val eucKrText = "<body>한국어</body>"
        val eucKrBytes = eucKrText.toByteArray(Charsets.forName("euc-kr"))
        val charArray = CharArray(eucKrText.length)
        val charset = Charsets.forName("GB2312")
        println(charset)

        val jCodeResult = Charsets.forName("euc-kr").newDecoder().decode(ByteBufferFactory.wrap(eucKrBytes), CharBufferFactory.wrap(charArray), true)
        println("jCodeResult: $jCodeResult")

        charArray.fill('0')
        StandardCharsets.UTF_8.aliases()

        val codeResult = Charsets.forName("euc-kr").newDecoder().decode(
            ByteBufferFactory.wrap(eucKrBytes),
            CharBufferFactory.wrap(charArray),
            true
        )

        println("jCodeResult: $codeResult")
//        assertEquals(4, decodedBytesCount)
//        assertEquals("He", out.toString())
    }
}