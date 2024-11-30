package com.fleeksoft.io.inputstream

import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.FilterInputStream
import com.fleeksoft.io.InputStream
import kotlin.random.Random
import kotlin.test.Test

/*
* @test
* @bug 8080835 8139206 8254742
* @library /test/lib
* @build jdk.test.lib.RandomFactory
* @run main ReadNBytes
* @summary Basic test for InputStream.readNBytes
* @key randomness
*/
class ReadNBytes {
    companion object {
        private val generator: Random = Random
    }

    @Test
    fun main() {
        test()
        test(byteArrayOf(1, 2, 3))
        test(createRandomBytes(1024))
        for (shift in intArrayOf(13, 15, 17)) {
            for (offset in intArrayOf(-1, 0, 1)) {
                test(createRandomBytes((1 shl shift) + offset))
            }
        }

        test(-1)
        test(0)
        for (shift in intArrayOf(13, 15, 17)) {
            for (offset in intArrayOf(-1, 0, 1)) {
                test((1 shl shift) + offset)
            }
        }
    }

    fun test(inputBytes: ByteArray) {
        val length = inputBytes.size
        val `in` = WrapperInputStream(ByteArrayInputStream(inputBytes))
        val readBytes = ByteArray((length / 2) + 1)
        var nread: Int = `in`.readNBytes(readBytes, 0, readBytes.size)

        var x: Int
        var tmp: ByteArray
        check(
            nread == readBytes.size,
            "Expected number of bytes read: ${readBytes.size}, got: $nread"
        )
        tmp = inputBytes.copyOf(nread)
        check(
            tmp.contentEquals(readBytes),
            "Expected[$tmp], got:[$readBytes]"
        )
        check(!`in`.isClosed, "Stream unexpectedly closed")

        // Read again
        nread = `in`.readNBytes(readBytes, 0, readBytes.size)

        check(
            nread == length - readBytes.size,
            "Expected number of bytes read: " + (length - readBytes.size) + ", got: " + nread
        )
        tmp = inputBytes.copyOfRange(readBytes.size, length)

        check(
            tmp.contentEquals(readBytes.copyOf(nread)),
            "Expected[$tmp], got:[$readBytes]"
        )
        // Expect end of stream
        check(
            (`in`.read().also { x = it }) == -1,
            "Expected end of stream from read(), got $x"
        )
        check(
            (`in`.read(tmp).also { x = it }) == -1,
            "Expected end of stream from read(byte[]), got $x"
        )
        check(
            (`in`.read(tmp, 0, tmp.size).also { x = it }) == -1,
            "Expected end of stream from read(byte[], int, int), got $x"
        )
        check(
            (`in`.readNBytes(tmp, 0, tmp.size).also { x = it }) == 0,
            "Expected end of stream, 0, from readNBytes(byte[], int, int), got $x"
        )
        check(!`in`.isClosed, "Stream unexpectedly closed")
    }

    fun test(max: Int) {
        val inputBytes = if (max <= 0) ByteArray(0) else createRandomBytes(max)
        val `in` =
            WrapperInputStream(ByteArrayInputStream(inputBytes))

        if (max < 0) {
            try {
                `in`.readNBytes(max)
                check(false, "Expected IllegalArgumentException not thrown")
            } catch (iae: IllegalArgumentException) {
                return
            }
        } else if (max == 0) {
            val x: Int
            check(
                (`in`.readNBytes(max).size.also { x = it }) == 0,
                "Expected zero bytes, got $x"
            )
            return
        }

        val off: Int = `in`.skip(generator.nextInt(max / 2).toLong()).toInt()
        val len: Int = generator.nextInt(max - 1 - off)
        var readBytes: ByteArray = `in`.readNBytes(len)
        check(
            readBytes.size == len,
            "Expected " + len + " bytes, got " + readBytes.size
        )
        val expectedBytes = inputBytes.copyOfRange(off, off + len)
        val actualBytes = readBytes.copyOfRange(0, len)


        check(
            expectedBytes.contentEquals(actualBytes)
        ) {
            "Expected: [${expectedBytes.joinToString()}], got: [${readBytes.joinToString()}]"
        }

        val remaining = max - (off + len)
        readBytes = `in`.readNBytes(remaining)
        check(
            readBytes.size == remaining,
            "Expected " + remaining + "bytes, got " + readBytes.size
        )
        val expectedBytes2 = inputBytes.copyOfRange(off + len, max)
        val actualBytes2 = readBytes.copyOfRange(0, remaining)

        check(
            expectedBytes2.contentEquals(actualBytes2)
        ) {
            "Expected: [${expectedBytes2.joinToString()}], got: [${readBytes.joinToString()}]"
        }

        check(!`in`.isClosed, "Stream unexpectedly closed")
    }

    fun test() {
        val chunkSize = 8192
        val size: Int = (10 + generator.nextInt(11)) * chunkSize

        val buf = ByteArray(size)
        generator.nextBytes(buf)
        val s: InputStream = ThrottledByteArrayInputStream(buf)

        val b: ByteArray? = s.readNBytes(size)

        check(b.contentEquals(buf), "Arrays not equal")
    }

    fun createRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        generator.nextBytes(bytes)
        return bytes
    }

    fun check(cond: Boolean, vararg failedArgs: String) {
        if (cond) return
        val sb = StringBuilder()
        for (o in failedArgs) sb.append(o)
        throw RuntimeException(sb.toString())
    }

    internal class WrapperInputStream(private val `in`: InputStream) : FilterInputStream(`in`) {
        var isClosed: Boolean = false
            private set

        override fun close() {
            this.isClosed = true
            `in`.close()
        }
    }

    internal class ThrottledByteArrayInputStream(buf: ByteArray) : ByteArrayInputStream(buf) {
        var count = 0

        // Sometimes return zero or a smaller count than requested.
        override fun read(buf: ByteArray, off: Int, len: Int): Int {
            var len = len
            if (generator.nextBoolean()) {
                return 0
            } else if (++count / 3 == 0) {
                len /= 3
            }

            return super.read(buf, off, len)
        }
    }
}
