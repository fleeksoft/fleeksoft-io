package com.fleeksoft.io

import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderTest {

    /*@Test
    fun testUtf16BE() = runTest {
        val firstLine = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">"""
        val input = TestHelper.readResource("bomtests/bom_utf16be.html")
            .reader(charset = Charsets.forName("UTF-16BE"))

//            ignore first char (ZWNBSP)\uFEFF:65279
        val strSize = firstLine.length + 1
        val charBuffer = CharArray(strSize)
        assertEquals(strSize, input.read(charBuffer, 0, charBuffer.size))
        val actualReadLine = charBuffer.concatToString()
        assertEquals(firstLine.length, actualReadLine.length - 1)
        assertEquals(firstLine, actualReadLine.substring(1))
    }

    @Test
    fun testUtf16LE() = runTest {

        val firstLine = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">"""
        val input = TestHelper.readResource("bomtests/bom_utf16le.html")
            .reader(charset = Charsets.forName("UTF-16LE"))

        //            ignore first char (ZWNBSP)\uFEFF:65279
        val strSize = firstLine.length + 1
        val charBuffer = CharArray(strSize)
        assertEquals(strSize, input.read(charBuffer, 0, charBuffer.size))
        val actualReadLine = charBuffer.concatToString()
        assertEquals(firstLine.length, actualReadLine.length - 1)
        assertEquals(firstLine, actualReadLine.substring(1))
    }*/

    @Test
    fun testSpuriousByteReader() {
        val html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>"
        val bufferedReader = html.byteInputStream().bufferedReader()
        html.forEach {
            assertEquals(it, bufferedReader.read().toChar())
        }

        val bufferedReader1 = html.byteInputStream().bufferedReader()
        val actual = bufferedReader1.readString(html.length)
        assertEquals(html, actual)


        val bufferedReader2 = html.byteInputStream().bufferedReader()
        bufferedReader2.skip(1)
        assertEquals(html.substring(1), bufferedReader2.readString(html.length - 1))
    }

    private fun readerStringTestStarter(input: String, testBody: (input: String, reader: Reader) -> Unit) {
        testBody(input, StringReader(input))
        testBody(input, BufferedReader(StringReader(input)))
        testBody(input, input.byteInputStream().bufferedReader())
    }

    @Test
    fun testCharReaderMarkSkipReset() = readerStringTestStarter("abcdefghijklm") { input, reader ->

        reader.mark(1111)
        val charArray = CharArray(3)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("abc", charArray.concatToString())
        reader.reset()
//            charReader.skip(3)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("abc", charArray.concatToString())

        reader.mark(1111)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("def", charArray.concatToString())
        reader.reset()
//            charReader.skip(3)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("def", charArray.concatToString())

        reader.mark(1111)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("ghi", charArray.concatToString())
        reader.reset()
//            charReader.skip(3)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("ghi", charArray.concatToString())

        reader.mark(1111)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("jkl", charArray.concatToString())
        reader.reset()
//            charReader.skip(3)
        assertEquals(3, reader.read(charArray, 0, 3))
        assertEquals("jkl", charArray.concatToString())

        reader.mark(1111)
        assertEquals(1, reader.read(charArray, 0, 3))
        assertEquals("mkl", charArray.concatToString())
        reader.reset()
        assertEquals(1, reader.read(charArray, 0, 3))
        assertEquals("mkl", charArray.concatToString())
    }


    @Test
    fun testCharSequence() = readerStringTestStarter("abcdefghijklmnopqrstuvwxyz") { input, reader ->
        input.forEach {
            assertEquals(it, reader.read().toChar())
        }
    }

    /*@Test
    fun testRandomLargeCharSequence() {
        (1..100000).forEach {
            println("testRandomLargeCharSequence: $it")
            readerStringTestStarter("abcdefghijklmnopqrstuvwxyz".repeat(it)) { input, reader ->
                input.forEach {
                    assertEquals(it, reader.read().toChar())
                }
            }
        }
    }*/

    @Test
    fun testLargeCharSequence() =
        readerStringTestStarter("abcdefghijklmnopqrstuvwxyz".repeat((10..500).random())) { input, reader ->
            input.forEach {
                assertEquals(it, reader.read().toChar())
            }
        }

    @Test
    fun testCharArrayRead() = readerStringTestStarter("abcdefghijklmnopqrstuvwxyz") { input, reader ->
        run {
            val charArray = CharArray(7)
            assertEquals(7, reader.read(charArray, 0, 7))
            assertEquals(input.substring(0..6), charArray.concatToString())
        }

        run {
            val charArray = CharArray(7)
            assertEquals(7, reader.read(charArray, 0, 7))
            assertEquals(input.substring(7..13), charArray.concatToString())
        }

        run {
            val charArray = CharArray(12)
            assertEquals(12, reader.read(charArray, 0, 12))
            assertEquals(input.substring(14..25), charArray.concatToString())
        }
    }

    @Test
    fun testMarkableCharReader() = readerStringTestStarter("abcdefghijklmnopqrstuvwxyz") { input, reader ->
        assertEquals('a', reader.read().toChar())
        assertEquals('b', reader.read().toChar())
        reader.mark(100)
        assertEquals('c', reader.read().toChar())
        assertEquals('d', reader.read().toChar())
        reader.reset()
        assertEquals('c', reader.read().toChar())
        assertEquals('d', reader.read().toChar())
        assertEquals('e', reader.read().toChar())
    }

    @Test
    fun testSkipCharReader() = readerStringTestStarter("abcdefghijklmnopqrstuvwxyz") { input, reader ->
        assertEquals('a', reader.read().toChar())
        assertEquals('b', reader.read().toChar())
        reader.skip(3)
        assertEquals('f', reader.read().toChar())
        assertEquals('g', reader.read().toChar())
        assertEquals('h', reader.read().toChar())
        reader.skip(2)
        assertEquals('k', reader.read().toChar())
        assertEquals('l', reader.read().toChar())
        assertEquals('m', reader.read().toChar())
    }

    @Test
    fun testMarkableSkipReader() = readerStringTestStarter("abcdefghijklmnopqrstuvwxyz") { input, reader ->
        assertEquals('a', reader.read().toChar())
        assertEquals('b', reader.read().toChar())
        assertEquals('c', reader.read().toChar())
        reader.skip(2)
        reader.mark(100)
        assertEquals('f', reader.read().toChar())
        assertEquals('g', reader.read().toChar())
        assertEquals('h', reader.read().toChar())
        reader.reset()
        assertEquals('f', reader.read().toChar())
        assertEquals('g', reader.read().toChar())
        assertEquals('h', reader.read().toChar())
        assertEquals('i', reader.read().toChar())
        assertEquals('j', reader.read().toChar())
        assertEquals('k', reader.read().toChar())
    }

    private fun testMixCharReader(inputData: String) = readerStringTestStarter(inputData) { inputData, reader ->
        inputData.forEach { char ->
            val charArray = CharArray(1)
            assertEquals(1, reader.read(charArray, 0, 1))
            assertEquals(char, charArray[0])
        }
        val charArray = CharArray(1) { ' ' }
        assertEquals(-1, reader.read(charArray, 0, 1))
        assertEquals(' ', charArray[0])
    }

    @Test
    fun testMixCharReader() {
        val inputData = "ä<a>ä</a>"
        testMixCharReader(inputData)
    }

    @Test
    fun testMixCharReader2() {
        val inputData = "한국어"
        testMixCharReader(inputData)
    }

    @Test
    fun testMixCharReader2Large() {
        val inputData = "한국어".repeat(10000)
        testMixCharReader(inputData)
    }

    @Test
    fun testMixCharReader3() {
        val inputData = "Übergrößenträger"
        testMixCharReader(inputData)
    }

    @Test
    fun testUtf16Charset() {
        val inputData = "ABCあ💩".repeat(29)
        testMixCharReader(inputData)
    }
}