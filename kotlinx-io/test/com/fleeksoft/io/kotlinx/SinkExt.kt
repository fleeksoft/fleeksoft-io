package com.fleeksoft.io.kotlinx

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import kotlinx.io.Sink
import kotlinx.io.writeString

public fun Sink.writeString(string: String, charset: Charset, startIndex: Int = 0, endIndex: Int = string.length) {
    checkBounds(string.length, startIndex, endIndex)
    if (charset == Charsets.UTF8) return writeString(string, startIndex, endIndex)
    val data = string.substring(startIndex, endIndex).toByteArray(charset)
    write(data, 0, data.size)
}

internal inline fun checkBounds(size: Int, startIndex: Int, endIndex: Int) =
    checkBounds(size.toLong(), startIndex.toLong(), endIndex.toLong())

internal fun checkBounds(size: Long, startIndex: Long, endIndex: Long) {
    if (startIndex < 0 || endIndex > size) {
        throw IndexOutOfBoundsException(
            "startIndex ($startIndex) and endIndex ($endIndex) are not within the range [0..size($size))"
        )
    }
    if (startIndex > endIndex) {
        throw IllegalArgumentException("startIndex ($startIndex) > endIndex ($endIndex)")
    }
}