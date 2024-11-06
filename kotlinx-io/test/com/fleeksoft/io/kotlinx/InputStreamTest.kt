package com.fleeksoft.io.kotlinx

import com.fleeksoft.io.ByteArrayInputStream
import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InputStreamTest {

    @Test
    fun inputStreamSource() {
        val bais = ByteArrayInputStream(byteArrayOf(0x61))
        val source = bais.asSource()
        val buffer = Buffer()
        source.readAtMostTo(buffer, 1)
        assertEquals(buffer.readString(), "a")
    }

    @Test
    fun inputStreamSourceReadZeroBytes() {
        val bais = ByteArrayInputStream(ByteArray(128))
        val source = bais.asSource()
        val buffer = Buffer()
        source.readAtMostTo(buffer, 0)
        assertEquals(0, buffer.size)
    }

    @Test
    fun inputStreamSourceReadNegativeNumberOfBytes() {
        val bais = ByteArrayInputStream(ByteArray(128))
        val source = bais.asSource()
        assertFailsWith<IllegalArgumentException> { source.readAtMostTo(Buffer(), -1) }
    }
}