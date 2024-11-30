package com.fleeksoft.io.kotlinx

import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.decodeToString
import com.fleeksoft.charset.toByteArray
import kotlin.test.assertEquals

fun assertByteArrayEquals(expectedUtf8: String, b: ByteArray) {
    assertEquals(expectedUtf8, b.decodeToString(Charsets.UTF8))
}

fun String.asUtf8ToByteArray(): ByteArray = this.toByteArray(Charsets.UTF8)

private fun fromHexChar(char: Char): Int {
    val code = char.code
    return when (code) {
        in '0'.code..'9'.code -> code - '0'.code
        in 'a'.code..'f'.code -> code - 'a'.code + 10
        in 'A'.code..'F'.code -> code - 'A'.code + 10
        else -> throw NumberFormatException("Not a hexadecimal digit: $char")
    }
}

fun String.decodeHex(): ByteArray {
    if (length % 2 != 0) throw IllegalArgumentException("Even number of bytes is expected.")

    val result = ByteArray(length / 2)

    for (idx in result.indices) {
        val byte = fromHexChar(this[idx * 2]).shl(4).or(fromHexChar(this[idx * 2 + 1]))
        result[idx] = byte.toByte()
    }

    return result
}
