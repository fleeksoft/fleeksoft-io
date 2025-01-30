package com.fleeksoft.charset.ext

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import com.fleeksoft.charset.toByteArray
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CharsetEncodingDecodingTest {
    @Test
    fun testGb18030EncodingDecoding() {
        val original = "你好, 世界!" // "Hello, World!" in Chinese
        val expectedBytes = byteArrayOf(-60, -29, -70, -61, 44, 32, -54, -64, -67, -25, 33)
        testEncodingDecoding("GB18030", original, expectedBytes)
    }
}