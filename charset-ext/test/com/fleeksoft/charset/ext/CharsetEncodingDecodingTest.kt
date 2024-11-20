package com.fleeksoft.charset.ext

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import com.fleeksoft.charset.toByteArray
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CharsetEncodingDecodingTest {

    private fun testEncodingDecoding(charsetName: String, original: String, expectedBytes: ByteArray) {
        // Retrieve the charset using forName
        val charset = Charsets.forName(charsetName)
        // Encode the original string to bytes using the provided charset
        val encoded: ByteArray = original.toByteArray(charset) // This line is used for encoding
        // Decode the byte array back to a string using the same charset
        val decoded = expectedBytes.decodeToString(charset)
        // Assert that the decoded string matches the original string
        assertEquals(original, decoded)
        // Assert that the encoded bytes are equal to the expected bytes
        assertContentEquals(expectedBytes, encoded)
    }

    @Test
    fun testUtf8EncodingDecoding() {
        val original = "Hello, 世界!"
        val expectedBytes = byteArrayOf(72, 101, 108, 108, 111, 44, 32, -28, -72, -106, -25, -107, -116, 33)
        testEncodingDecoding("UTF-8", original, expectedBytes)
    }

    @Test
    fun testUtf16EncodingDecoding() {
        val original = "Hello, 世界!"
        val expectedBytes =
            byteArrayOf(-2, -1, 0, 72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 44, 0, 32, 78, 22, 117, 76, 0, 33)
        testEncodingDecoding("UTF-16", original, expectedBytes)
    }

    @Test
    fun testUtf16LEEncodingDecoding() {
        val original = "Hello, 世界!"
        val expectedBytes = byteArrayOf(72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 44, 0, 32, 0, 22, 78, 76, 117, 33, 0)
        testEncodingDecoding("UTF-16LE", original, expectedBytes)
    }

    @Test
    fun testUtf16BEEncodingDecoding() {
        val original = "Hello, 世界!"
        val expectedBytes = byteArrayOf(0, 72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 44, 0, 32, 78, 22, 117, 76, 0, 33)
        testEncodingDecoding("UTF-16BE", original, expectedBytes)
    }

    @Test
    fun testUtf32EncodingDecoding() {
        val original = "Hello, Świat!"
        val expectedBytes = byteArrayOf(
            0,
            0,
            0,
            72,
            0,
            0,
            0,
            101,
            0,
            0,
            0,
            108,
            0,
            0,
            0,
            108,
            0,
            0,
            0,
            111,
            0,
            0,
            0,
            44,
            0,
            0,
            0,
            32,
            0,
            0,
            1,
            90,
            0,
            0,
            0,
            119,
            0,
            0,
            0,
            105,
            0,
            0,
            0,
            97,
            0,
            0,
            0,
            116,
            0,
            0,
            0,
            33
        )
        testEncodingDecoding("UTF-32", original, expectedBytes)
    }

    @Test
    fun testIso88591EncodingDecoding() {
        val original = "Hello, World!"
        val expectedBytes = byteArrayOf(
            72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33
        )
        testEncodingDecoding("ISO-8859-1", original, expectedBytes)
    }

    @Test
    fun testIso88592EncodingDecoding() {
        val original = "Hello, Świat!"
        val expectedBytes = byteArrayOf(
            72, 101, 108, 108, 111, 44, 32,  // "Hello, "
            -90, 119, 105, 97, 116, 33 // "Świat!"
        )
        testEncodingDecoding("ISO-8859-2", original, expectedBytes)
    }

    @Test
    fun testWindows1252EncodingDecoding() {
        val original = "Hello, World!"
        val expectedBytes = byteArrayOf(
            72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33
        )
        testEncodingDecoding("windows-1252", original, expectedBytes)
    }

    @Test
    fun testAsciiEncodingDecoding() {
        val original = "Hello!"
        val expectedBytes = byteArrayOf(
            72, 101, 108, 108, 111, 33
        )
        testEncodingDecoding("US-ASCII", original, expectedBytes)
    }

    @Test
    fun testKoi8rEncodingDecoding() {
        val original = "Привет!" // "Hello" in Russian
        val expectedBytes = byteArrayOf(-16, -46, -55, -41, -59, -44, 33)
        testEncodingDecoding("KOI8-R", original, expectedBytes)
    }

    @Test
    fun testKoi8uEncodingDecoding() {
        val original = "Привет!" // "Hello" in Russian
        val expectedBytes = byteArrayOf(-16, -46, -55, -41, -59, -44, 33)
        testEncodingDecoding("KOI8-U", original, expectedBytes)
    }

    @Test
    fun testCesu8EncodingDecoding() {
        val original = "Hello, 世界!"
        val expectedBytes = byteArrayOf(72, 101, 108, 108, 111, 44, 32, -28, -72, -106, -25, -107, -116, 33)
        testEncodingDecoding("CESU-8", original, expectedBytes)
    }

    @Test
    fun testGb18030EncodingDecoding() {
        val original = "你好, 世界!" // "Hello, World!" in Chinese
        val expectedBytes = byteArrayOf(-60, -29, -70, -61, 44, 32, -54, -64, -67, -25, 33)
        testEncodingDecoding("GB18030", original, expectedBytes)
    }
}