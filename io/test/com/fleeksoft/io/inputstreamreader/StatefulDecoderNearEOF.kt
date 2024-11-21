package com.fleeksoft.io.inputstreamreader

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.charset.Platform
import com.fleeksoft.charset.isJvmOrAndroid
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.InputStreamReader
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.exception.MalformedInputException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StatefulDecoderNearEOF {
    @Test
    fun testStateFulDecoderNearEOF() {
        inputs().forEach {
            testStatefulDecoderNearEOF(it[0] as ByteArray, it[1] as Int)
        }
    }

    fun inputs(): Array<Array<Any>> {
        return arrayOf(
            // BOM, followed by High surrogate (in UTF-16LE).
            // First read() should throw an exception.
            arrayOf(
                byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0xd8.toByte()),
                0
            ),  // BOM, followed by 'A', 'B', 'C', then by High surrogate (in UTF-16LE).
            // Fourth read() should throw an exception.

            arrayOf(byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0x41.toByte(), 0, 0x42.toByte(), 0, 0x43.toByte(), 0, 0, 0xd8.toByte()), 3),
        )
    }

    fun testStatefulDecoderNearEOF(ba: ByteArray, numSucessReads: Int) {
        if (Platform.isJvmOrAndroid()) {
            // FIXME: allow this from jdk21
            return
        }
        InputStreamReader(
            ByteArrayInputStream(ba),
            Charsets.forName("UTF-16").newDecoder().onMalformedInput(CodingErrorActionValue.REPORT)
        ).use { r ->
            // Issue read() as many as numSucessReads which should not fail
            (1..numSucessReads).forEach { i ->
                try {
                    assertEquals(r.read(), ba[i * 2].toInt())
                } catch (e: IOException) {
                    throw Exception(e)
                }
            }

            // Final dangling high surrogate should throw an exception
            assertFailsWith<MalformedInputException> {
                r.read()
            }
        }
    }
}
