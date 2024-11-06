package com.fleeksoft.io.kotlinx

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered

interface SourceFactory {
    class Pipe(
        var sink: Sink,
        var source: Source
    )

    val isOneByteAtATime: Boolean

    fun pipe(): Pipe

    companion object {
        val BUFFER: SourceFactory = object : SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = false

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    buffer,
                    buffer
                )
            }
        }

        val REAL_BUFFERED_SOURCE: SourceFactory = object :
            SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = false

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    buffer,
                    (buffer as RawSource).buffered()
                )
            }
        }

        /**
         * A factory deliberately written to create buffers whose internal segments are always 1 byte
         * long. We like testing with these segments because are likely to trigger bugs!
         */
        val ONE_BYTE_AT_A_TIME_BUFFERED_SOURCE: SourceFactory = object :
            SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = true

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    buffer,
                    object : RawSource by buffer {
                        override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
                            // Read one byte into a new buffer, then clone it so that the segment is shared.
                            // Shared segments cannot be compacted so we'll get a long chain of short segments.
                            val box = Buffer()
                            val result = buffer.readAtMostTo(box, minOf(byteCount, 1L))
                            if (result > 0L) sink.write(box.copy(), result)
                            return result
                        }
                    }.buffered()
                )
            }
        }

        val ONE_BYTE_AT_A_TIME_BUFFER: SourceFactory = object :
            SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = true

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    object : RawSink by buffer {
                        override fun write(source: Buffer, byteCount: Long) {
                            // Write each byte into a new buffer, then clone it so that the segments are shared.
                            // Shared segments cannot be compacted so we'll get a long chain of short segments.
                            for (i in 0 until byteCount) {
                                val box = Buffer()
                                box.write(source, 1)
                                buffer.write(box.copy(), 1)
                            }
                        }
                    }.buffered(),
                    buffer
                )
            }
        }

        val PEEK_BUFFER: SourceFactory = object : SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = false

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    buffer,
                    buffer.peek()
                )
            }
        }

        val PEEK_BUFFERED_SOURCE: SourceFactory = object :
            SourceFactory {

            override val isOneByteAtATime: Boolean
                get() = false

            override fun pipe(): Pipe {
                val buffer = Buffer()
                return Pipe(
                    buffer,
                    (buffer as RawSource).buffered().peek()
                )
            }
        }

        val PARAMETERIZED_TEST_VALUES = mutableListOf<Array<Any>>(
            arrayOf(BUFFER),
            arrayOf(REAL_BUFFERED_SOURCE),
            arrayOf(ONE_BYTE_AT_A_TIME_BUFFERED_SOURCE),
            arrayOf(ONE_BYTE_AT_A_TIME_BUFFER),
            arrayOf(PEEK_BUFFER),
            arrayOf(PEEK_BUFFERED_SOURCE)
        )
    }
}
