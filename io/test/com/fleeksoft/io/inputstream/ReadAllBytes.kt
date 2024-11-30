package com.fleeksoft.io.inputstream

import com.fleeksoft.io.ByteArrayInputStream
import com.fleeksoft.io.FilterInputStream
import com.fleeksoft.io.InputStream
import kotlin.random.Random
import kotlin.test.Test

/*
* @test
* @bug 8080835 8193832
* @library /test/lib
* @build jdk.test.lib.RandomFactory
* @run main ReadAllBytes
* @summary Basic test for InputStream.readAllBytes
* @key randomness
*/
class ReadAllBytes {
    companion object {
        private val generator: Random = Random
    }

    @Test
    fun main() {
        val bytes = ByteArray(16385)
        Random.nextBytes(bytes)
        val input = WrapperInputStream(ByteArrayInputStream(bytes))
        val readBytes: ByteArray = input.readAllBytes()
        return

        test(byteArrayOf())
        test(byteArrayOf(1, 2, 3))
        test(createRandomBytes(1024))
        for (shift in intArrayOf(13, 14, 15, 17)) {
            for (offset in intArrayOf(-1, 0, 1)) {
                test(createRandomBytes((1 shl shift) + offset))
            }
        }
    }

    fun test(expectedBytes: ByteArray) {
        val expectedLength = expectedBytes.size
        println("ReadAllBytes expectedLength: $expectedLength")
        val input = WrapperInputStream(ByteArrayInputStream(expectedBytes))
        val readBytes: ByteArray = input.readAllBytes()

        var x: Int
        val tmp = ByteArray(10)
        check(
            (input.read().also { x = it }) == -1,
            "Expected end of stream from read(), got $x"
        )
        check(
            (input.read(tmp).also { x = it }) == -1,
            "Expected end of stream from read(byte[]), got $x"
        )
        check(
            (input.read(tmp, 0, tmp.size).also { x = it }) == -1,
            "Expected end of stream from read(byte[], int, int), got $x"
        )
        check(
            input.readAllBytes().isEmpty(),
            "Expected readAllBytes to return empty byte array"
        )
        check(
            expectedLength == readBytes.size,
            "Expected length " + expectedLength + ", got " + readBytes.size
        )
        check(
            expectedBytes.contentEquals(readBytes),
            "Expected[$expectedBytes], got:[$readBytes]"
        )
        check(!input.isClosed, "Stream unexpectedly closed")
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

    internal class WrapperInputStream(private val inputStream: InputStream) : FilterInputStream(inputStream) {
        var isClosed: Boolean = false
            private set

        override fun close() {
            this.isClosed = true
            inputStream.close()
        }
    }
}
