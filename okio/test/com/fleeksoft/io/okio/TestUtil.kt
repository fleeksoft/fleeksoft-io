package com.fleeksoft.io.okio

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import kotlin.jvm.JvmStatic
import kotlin.test.assertEquals

object TestUtil {
    @JvmStatic
    fun assertByteArrayEquals(expectedUtf8: String, b: ByteArray) {
        assertEquals(expectedUtf8, b.decodeToString(Charsets.UTF8))
    }
}