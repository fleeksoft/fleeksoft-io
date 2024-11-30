package com.fleeksoft.io.kotlinx

import kotlinx.io.Buffer
import kotlinx.io.EOFException
import kotlinx.io.IOException
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.io.ByteBufferFactory
import com.fleeksoft.io.flipExt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BufferSourceTest : AbstractSourceTest(SourceFactory.BUFFER)
class RealBufferedSourceTest : AbstractSourceTest(SourceFactory.REAL_BUFFERED_SOURCE)
class OneByteAtATimeBufferedSourceTest : AbstractSourceTest(SourceFactory.ONE_BYTE_AT_A_TIME_BUFFERED_SOURCE)
class OneByteAtATimeBufferTest : AbstractSourceTest(SourceFactory.ONE_BYTE_AT_A_TIME_BUFFER)
class PeekBufferTest : AbstractSourceTest(SourceFactory.PEEK_BUFFER)
class PeekBufferedSourceTest : AbstractSourceTest(SourceFactory.PEEK_BUFFERED_SOURCE)

const val SEGMENT_SIZE = 8192

abstract class AbstractSourceTest(private val factory: SourceFactory) {
    private var sink: Sink
    private var source: Source

    init {
        val pipe: SourceFactory.Pipe = factory.pipe()
        sink = pipe.sink
        source = pipe.source
    }

    @Test
    fun inputStream() {
        sink.writeString("abc")
        sink.emit()
        val input: InputStream = source.asInputStream()
        val bytes = byteArrayOf('z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte())
        var read: Int = input.read(bytes)
        if (factory.isOneByteAtATime) {
            assertEquals(1, read)
            assertByteArrayEquals("azz", bytes)
            read = input.read(bytes)
            assertEquals(1, read)
            assertByteArrayEquals("bzz", bytes)
            read = input.read(bytes)
            assertEquals(1, read)
            assertByteArrayEquals("czz", bytes)
        } else {
            assertEquals(3, read)
            assertByteArrayEquals("abc", bytes)
        }
        assertEquals(-1, input.read())
    }

    @Test
    fun inputStreamOffsetCount() {
        sink.writeString("abcde")
        sink.emit()
        val input: InputStream = source.asInputStream()
        val bytes =
            byteArrayOf('z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte())
        val read: Int = input.read(bytes, 1, 3)
        if (factory.isOneByteAtATime) {
            assertEquals(1, read)
            assertByteArrayEquals("zazzz", bytes)
        } else {
            assertEquals(3, read)
            assertByteArrayEquals("zabcz", bytes)
        }
    }

    @Test
    fun inputStreamSkip() {
        sink.writeString("abcde")
        sink.emit()
        val input: InputStream = source.asInputStream()
        assertEquals(4, input.skip(4))
        assertEquals('e'.code, input.read())
        sink.writeString("abcde")
        sink.emit()
        assertEquals(5, input.skip(10)) // Try to skip too much.
        assertEquals(0, input.skip(1)) // Try to skip when exhausted.
    }

    @Test
    fun inputStreamCharByChar() {
        sink.writeString("abc")
        sink.emit()
        val input: InputStream = source.asInputStream()
        assertEquals('a'.code, input.read())
        assertEquals('b'.code, input.read())
        assertEquals('c'.code, input.read())
        assertEquals(-1, input.read())
    }

    @Test
    fun inputStreamBounds() {
        sink.writeString("a".repeat(100))
        sink.emit()
        val input: InputStream = source.asInputStream()
        assertFailsWith<IndexOutOfBoundsException> {
            input.read(ByteArray(100), 50, 51)
        }
    }

    @Test
    fun inputStreamForClosedSource() {
        if (source is Buffer) {
            return
        }

        sink.writeByte(0)
        sink.emit()

        val input = source.asInputStream()
        source.close()
        assertFailsWith<Exception> { input.read() }
        // FIXME: not success for all
//        assertFailsWith<Exception> { input.read(ByteArray(1)) }
//        assertFailsWith<Exception> { input.read(ByteArray(10), 0, 1) }
    }

    @Test
    fun inputStreamClosesSource() {
        if (source is Buffer) {
            return
        }

        sink.writeByte(0)
        sink.emit()

        val input = source.asInputStream()
        input.close()

        assertFailsWith<IllegalStateException> { source.readByte() }
    }

    @Test
    fun inputStreamAvailable() {
        val input = source.asInputStream()
        assertEquals(0, input.available())

        sink.writeInt(42)
        sink.emit()
        assertTrue(source.request(4)) // fill the buffer

        assertEquals(4, input.available())

        input.read()
        assertEquals(3, input.available())

        source.readByte()
        assertEquals(2, input.available())

        sink.writeByte(0)
        sink.emit()

        val expectedBytes = if (source is Buffer) {
            3
        } else {
            2
        }
        assertEquals(expectedBytes, input.available())
    }

    /*@Test
    fun inputStreamAvailableForClosedSource() {
        if (source is Buffer) {
            return
        }

        val input = source.asInputStream()
        source.close()

        assertFailsWith<IOException> { input.available() }
    }*/

    @Test
    fun readNioBuffer() {
        val expected = if (factory.isOneByteAtATime) "a" else "abcdefg"
        sink.writeString("abcdefg")
        sink.emit()
        val nioByteBuffer: ByteBuffer = ByteBufferFactory.allocate(1024)
        val byteCount: Int = source.readAtMostTo(nioByteBuffer)
        assertEquals(expected.length, byteCount)
        assertEquals(expected.length, nioByteBuffer.position())
        assertEquals(nioByteBuffer.capacity(), nioByteBuffer.limit())
        nioByteBuffer.flipExt() // Cast necessary for Java 8.
        val data = ByteArray(expected.length)
        nioByteBuffer.get(data)
        assertEquals(expected, data.decodeToString())
    }

    /** Note that this test crashes the VM on Android.  */
    @Test
    fun readLargeNioBufferOnlyReadsOneSegment() {
        val expected: String = if (factory.isOneByteAtATime) "a" else "a".repeat(SEGMENT_SIZE)
        sink.writeString("a".repeat(SEGMENT_SIZE * 4))
        sink.emit()
        val nioByteBuffer: ByteBuffer = ByteBufferFactory.allocate(SEGMENT_SIZE * 3)
        val byteCount: Int = source.readAtMostTo(nioByteBuffer)
        assertEquals(expected.length, byteCount)
        assertEquals(expected.length, nioByteBuffer.position())
        assertEquals(nioByteBuffer.capacity(), nioByteBuffer.limit())
        nioByteBuffer.flipExt() // Cast necessary for Java 8.
        val data = ByteArray(expected.length)
        nioByteBuffer.get(data)
        assertEquals(expected, data.decodeToString())
    }

    @Test
    fun readNioBufferFromEmptySource() {
        assertEquals(-1, source.readAtMostTo(ByteBufferFactory.allocate(10)))
    }

    @Test
    fun readSpecificCharsetPartial() {
        sink.write(
            ("0000007600000259000002c80000006c000000e40000007300000259000002" +
                    "cc000000720000006100000070000000740000025900000072").decodeHex()
        )
        sink.emit()
        assertEquals("vəˈläsə", source.readString(7 * 4, Charsets.forName("utf-32")))
    }

    @Test
    fun readSpecificCharset() {
        sink.write(
            ("0000007600000259000002c80000006c000000e40000007300000259000002" +
                    "cc000000720000006100000070000000740000025900000072").decodeHex()
        )

        sink.emit()
        assertEquals("vəˈläsəˌraptər", source.readString(Charsets.forName("utf-32")))
    }

    @Test
    fun readStringTooShortThrows() {
        sink.writeString("abc", Charsets.US_ASCII)
        sink.emit()
        assertFailsWith<EOFException> {
            source.readString(4, Charsets.US_ASCII)
        }
        assertEquals("abc", source.readString()) // The read shouldn't consume any data.
    }
}
