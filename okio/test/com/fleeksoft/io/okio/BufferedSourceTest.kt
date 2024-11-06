package com.fleeksoft.io.okio

import okio.*
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BufferedSourceTest {
    @Test
    fun inputStream() {
        BufferedSourceTestInternal.Factory.entries.forEach {
            BufferedSourceTestInternal(it).inputStream()
        }
    }

    @Test
    fun inputStreamOffsetCount() {
        BufferedSourceTestInternal.Factory.entries.forEach {
            BufferedSourceTestInternal(it).inputStreamOffsetCount()
        }
    }

    @Test
    fun inputStreamSkip() {
        BufferedSourceTestInternal.Factory.entries.forEach {
            BufferedSourceTestInternal(it).inputStreamSkip()
        }
    }

    @Test
    fun inputStreamCharByChar() {
        BufferedSourceTestInternal.Factory.entries.forEach {
            BufferedSourceTestInternal(it).inputStreamCharByChar()
        }
    }

    @Test
    fun inputStreamBounds() {
        BufferedSourceTestInternal.Factory.entries.forEach {
            BufferedSourceTestInternal(it).inputStreamBounds()
        }
    }
}

private class BufferedSourceTestInternal(private val factory: Factory) {
    enum class Factory {
        NewBuffer {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(buffer, buffer)
            }

            override val isOneByteAtATime: Boolean get() = false
        },

        SourceBuffer {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    sink = buffer,
                    source = (buffer as Source).buffer(),
                )
            }

            override val isOneByteAtATime: Boolean get() = false
        },

        /**
         * A factory deliberately written to create buffers whose internal segments are always 1 byte
         * long. We like testing with these segments because are likely to trigger bugs!
         */
        OneByteAtATimeSource {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    sink = buffer,
                    source = object : ForwardingSource(buffer) {
                        override fun read(sink: Buffer, byteCount: Long): Long {
                            // Read one byte into a new buffer, then clone it so that the segment is shared.
                            // Shared segments cannot be compacted so we'll get a long chain of short segments.
                            val box = Buffer()
                            val result = super.read(box, min(byteCount, 1L))
                            if (result > 0L) sink.write(box.peek(), result)
                            return result
                        }
                    }.buffer(),
                )
            }

            override val isOneByteAtATime: Boolean get() = true
        },

        OneByteAtATimeSink {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                val sink = object : Sink {
                    override fun close() = buffer.close()

                    override fun flush() = buffer.flush()

                    override fun timeout(): Timeout = buffer.timeout()

                    override fun write(source: Buffer, byteCount: Long) {
                        // Write each byte into a new buffer, then clone it so that the segments are shared.
                        // Shared segments cannot be compacted so we'll get a long chain of short segments.
                        (0 until byteCount).forEach { i ->
                            val box = Buffer()
                            box.write(source, 1)
                            buffer.write(box.peek(), 1)
                        }
                    }
                }.buffer()
                return Pipe(
                    sink = sink,
                    source = buffer,
                )
            }

            override val isOneByteAtATime: Boolean get() = true
        },

        PeekSource {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    sink = buffer,
                    source = buffer.peek(),
                )
            }

            override val isOneByteAtATime: Boolean get() = false
        },

        PeekBufferedSource {
            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    sink = buffer,
                    source = (buffer as Source).buffer().peek(),
                )
            }

            override val isOneByteAtATime: Boolean get() = false
        },
        ;

        abstract fun pipe(): Pipe
        abstract val isOneByteAtATime: Boolean
    }

    class Pipe(
        var sink: BufferedSink,
        var source: BufferedSource,
    )

    private val pipe = factory.pipe()
    private val sink: BufferedSink = pipe.sink
    private val source: BufferedSource = pipe.source

    fun inputStream() {
        sink.writeUtf8("abc")
        sink.emit()
        val `in` = source.asInputStream()
        val bytes = byteArrayOf('z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte())
        var read = `in`.read(bytes)
        if (factory.isOneByteAtATime) {
            assertEquals(1, read.toLong())
            TestUtil.assertByteArrayEquals("azz", bytes)
            read = `in`.read(bytes)
            assertEquals(1, read.toLong())
            TestUtil.assertByteArrayEquals("bzz", bytes)
            read = `in`.read(bytes)
            assertEquals(1, read.toLong())
            TestUtil.assertByteArrayEquals("czz", bytes)
        } else {
            assertEquals(3, read.toLong())
            TestUtil.assertByteArrayEquals("abc", bytes)
        }
        assertEquals(-1, `in`.read().toLong())
    }

    fun inputStreamOffsetCount() {
        sink.writeUtf8("abcde")
        sink.emit()
        val `in` = source.asInputStream()
        val bytes = byteArrayOf('z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte(), 'z'.code.toByte())
        val read = `in`.read(bytes, 1, 3)
        if (factory.isOneByteAtATime) {
            assertEquals(1, read.toLong())
            TestUtil.assertByteArrayEquals("zazzz", bytes)
        } else {
            assertEquals(3, read.toLong())
            TestUtil.assertByteArrayEquals("zabcz", bytes)
        }
    }

    fun inputStreamSkip() {
        sink.writeUtf8("abcde")
        sink.emit()
        val `in` = source.asInputStream()
        assertEquals(4, `in`.skip(4))
        assertEquals('e'.code.toLong(), `in`.read().toLong())
        sink.writeUtf8("abcde")
        sink.emit()
        assertEquals(5, `in`.skip(10)) // Try to skip too much.
        assertEquals(0, `in`.skip(1)) // Try to skip when exhausted.
    }

    fun inputStreamCharByChar() {
        sink.writeUtf8("abc")
        sink.emit()
        val `in` = source.asInputStream()
        assertEquals('a'.code.toLong(), `in`.read().toLong())
        assertEquals('b'.code.toLong(), `in`.read().toLong())
        assertEquals('c'.code.toLong(), `in`.read().toLong())
        assertEquals(-1, `in`.read().toLong())
    }

    fun inputStreamBounds() {
        sink.writeUtf8("a".repeat(100))
        sink.emit()
        val `in` = source.asInputStream()
        assertFailsWith<okio.ArrayIndexOutOfBoundsException> {
            `in`.read(ByteArray(100), 50, 51)
        }
    }
}