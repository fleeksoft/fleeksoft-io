package com.fleeksoft.charset

import com.fleeksoft.io.ByteBufferFactory

fun String.toByteArray(charset: Charset): ByteArray {
    val result = charset.encode(this)
    val existingArray = when {
        result.hasArray() && result.arrayOffset() == 0 -> result.array().takeIf { it.size == result.remaining() }
        else -> null
    }
    return existingArray ?: ByteArray(result.remaining()).also { result.get(it) }
}

fun ByteArray.decodeToString(charset: Charset, off: Int = 0, len: Int = this.size): String {
    val decode = charset.decode(ByteBufferFactory.wrap(this, off = off, len = len))
    return decode.toString()
}