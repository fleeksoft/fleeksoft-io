package com.fleeksoft.io

public inline fun String.byteInputStream(): ByteArrayInputStream =
    ByteArrayInputStream(this.encodeToByteArray())

public inline fun ByteArray.inputStream(): ByteArrayInputStream = ByteArrayInputStream(this)

public inline fun ByteArray.inputStream(off: Int, len: Int): ByteArrayInputStream =
    ByteArrayInputStream(this, off, len)

public inline fun String.reader(): StringReader = StringReader(this)

public fun Reader.readString(count: Int): String {
    val buffer = CharArray(count)
    val charsRead = this.read(buffer, 0, count)
    return if (charsRead > 0) buffer.concatToString(startIndex = 0, endIndex = charsRead) else ""
}