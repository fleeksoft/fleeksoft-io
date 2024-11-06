package com.fleeksoft.io.okio

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import com.fleeksoft.charset.toByteArray
import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.Constants
import com.fleeksoft.io.byteInputStream
import okio.Buffer
import okio.IOException
import okio.Source
import okio.buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class OkioTest {
    companion object {
        const val SEGMENT_SIZE: Int = Constants.SEGMENT_SIZE.toInt()
    }

    @Test
    fun inputStreamSource() {
        val bais = ByteArrayInputStream(byteArrayOf(0x61))
        val source = bais.asSource()
        val buffer = Buffer()
        source.read(buffer, 1)
        assertEquals(buffer.readUtf8(), "a")
    }

    @Test
    fun inputStreamTracksSegments() {
        val inputStream = "".byteInputStream()
        inputStream.asSource()
        val source = Buffer()
        source.writeUtf8("a")
        source.writeUtf8("b".repeat(SEGMENT_SIZE))
        source.writeUtf8("c")
        val `in` = (source as Source).buffer().asInputStream()
        assertEquals(0, `in`.available().toLong())
        assertEquals((SEGMENT_SIZE + 2).toLong(), source.size)

        // Reading one byte buffers a full segment.
        assertEquals('a'.code.toLong(), `in`.read().toLong())
        assertEquals((SEGMENT_SIZE - 1).toLong(), `in`.available().toLong())
        assertEquals(2, source.size)

        // Reading as much as possible reads the rest of that buffered segment.
        val data = ByteArray(SEGMENT_SIZE * 2)
        assertEquals((SEGMENT_SIZE - 1).toLong(), `in`.read(data, 0, data.size).toLong())
        assertEquals("b".repeat(SEGMENT_SIZE - 1), data.decodeToString(Charsets.UTF8, 0, SEGMENT_SIZE - 1))
        assertEquals(2, source.size)

        // Continuing to read buffers the next segment.
        assertEquals('b'.code.toLong(), `in`.read().toLong())
        assertEquals(1, `in`.available().toLong())
        assertEquals(0, source.size)

        // Continuing to read reads from the buffer.
        assertEquals('c'.code.toLong(), `in`.read().toLong())
        assertEquals(0, `in`.available().toLong())
        assertEquals(0, source.size)

        // Once we've exhausted the source, we're done.
        assertEquals(-1, `in`.read().toLong())
        assertEquals(0, source.size)
    }

    @Test
    fun inputStreamCloses() {
        val source = (Buffer() as Source).buffer()
        val inputStream = source.asInputStream()
        inputStream.close()
        try {
            source.require(1)
            fail()
        } catch (e: IllegalStateException) {
            assertEquals("closed", e.message)
        }
    }

    @Test
    fun sourceFromInputStream() {
        val inputStream = ByteArrayInputStream(
            ("a" + "b".repeat(SEGMENT_SIZE * 2) + "c").toByteArray(Charsets.UTF8),
        )

        // Source: ab...bc
        val source = inputStream.asSource()
        val sink = Buffer()

        // Source: b...bc. Sink: abb.
        assertEquals(3, source.read(sink, 3))
        assertEquals("abb", sink.readUtf8(3))

        // Source: b...bc. Sink: b...b.
        assertEquals(SEGMENT_SIZE.toLong(), source.read(sink, 20000))
        assertEquals("b".repeat(SEGMENT_SIZE), sink.readUtf8())

        // Source: b...bc. Sink: b...bc.
        assertEquals((SEGMENT_SIZE - 1).toLong(), source.read(sink, 20000))
        assertEquals("b".repeat(SEGMENT_SIZE - 2) + "c", sink.readUtf8())

        // Source and sink are empty.
        assertEquals(-1, source.read(sink, 1))
    }

    @Test
    fun sourceFromInputStreamWithSegmentSize() {
        val inputStream = ByteArrayInputStream(ByteArray(SEGMENT_SIZE))
        val source = inputStream.asSource()
        val sink = Buffer()
        assertEquals(SEGMENT_SIZE.toLong(), source.read(sink, SEGMENT_SIZE.toLong()))
        assertEquals(-1, source.read(sink, SEGMENT_SIZE.toLong()))
    }

    @Test
    fun sourceFromInputStreamBounds() {
        val source = ByteArrayInputStream(ByteArray(100)).asSource()
        try {
            source.read(Buffer(), -1)
            fail()
        } catch (expected: IllegalArgumentException) {
        }
    }


    @Test
    fun bufferInputStreamByteByByte() {
        val source = Buffer()
        source.writeUtf8("abc")
        val `in` = source.asInputStream()
        assertEquals(3, `in`.available().toLong())
        assertEquals('a'.code.toLong(), `in`.read().toLong())
        assertEquals('b'.code.toLong(), `in`.read().toLong())
        assertEquals('c'.code.toLong(), `in`.read().toLong())
        assertEquals(-1, `in`.read().toLong())
        assertEquals(0, `in`.available().toLong())
    }

    @Test
    fun bufferInputStreamBulkReads() {
        val source = Buffer()
        source.writeUtf8("abc")
        val byteArray = ByteArray(4)
        byteArray.fill(-5)
        val `in` = source.asInputStream()
        assertEquals(3, `in`.read(byteArray).toLong())
        assertEquals("[97, 98, 99, -5]", byteArray.contentToString())
        byteArray.fill(-7)
        assertEquals(-1, `in`.read(byteArray).toLong())
        assertEquals("[-7, -7, -7, -7]", byteArray.contentToString())
    }

    @Test
    fun operationsAfterClose() {
        val source = Buffer()
        val bufferedSource = (source as Source).buffer()
        bufferedSource.close()

        // Test a sample set of methods.
        try {
            bufferedSource.indexOf(1.toByte())
            fail()
        } catch (expected: IllegalStateException) {
        }
        try {
            bufferedSource.skip(1)
            fail()
        } catch (expected: IllegalStateException) {
        }
        try {
            bufferedSource.readByte()
            fail()
        } catch (expected: IllegalStateException) {
        }
        try {
            bufferedSource.readByteString(10)
            fail()
        } catch (expected: IllegalStateException) {
        }

        // Test a sample set of methods on the InputStream.
        val inputStream = bufferedSource.asInputStream()
        try {
            inputStream.read()
            fail()
        } catch (expected: IOException) {
        }
        try {
            inputStream.read(ByteArray(10))
            fail()
        } catch (expected: IOException) {
        }
    }
}