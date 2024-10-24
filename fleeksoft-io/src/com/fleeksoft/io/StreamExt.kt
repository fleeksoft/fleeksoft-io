@file:OptIn(ExperimentalContracts::class)

package com.fleeksoft.io

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun String.byteInputStream(charset: Charset = Charsets.UTF8): ByteArrayInputStream =
    ByteArrayInputStream(toByteArray(charset))

public inline fun ByteArray.inputStream(): ByteArrayInputStream = ByteArrayInputStream(this)

public inline fun ByteArray.inputStream(offset: Int, length: Int): ByteArrayInputStream =
    ByteArrayInputStream(this, offset, length)

public inline fun InputStream.reader(charset: Charset = Charsets.UTF8): InputStreamReader =
    InputStreamReader(this, charset)

public inline fun Reader.buffered(bufferSize: Int = Constants.DEFAULT_BYTE_BUFFER_SIZE): BufferedReader =
    if (this is BufferedReader) this else BufferedReader(this, bufferSize)

public inline fun InputStream.bufferedReader(charset: Charset = Charsets.UTF8): BufferedReader =
    reader(charset).buffered()


public fun Reader.forEachLine(action: (String) -> Unit): Unit = useLines { it.forEach(action) }

public fun Reader.readLines(): List<String> {
    val result = arrayListOf<String>()
    forEachLine { result.add(it) }
    return result
}

public fun Reader.readString(count: Int): String {
    val buffer = CharArray(count)
    val charsRead = this.read(buffer, 0, count)
    return if (charsRead > 0) buffer.concatToString(startIndex = 0, endIndex = charsRead) else ""
}

public inline fun <T> Reader.useLines(block: (Sequence<String>) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return buffered().use { block(it.lineSequence()) }
}

public inline fun String.reader(): StringReader = StringReader(this)

public fun BufferedReader.lineSequence(): Sequence<String> = LinesSequence(this).constrainOnce()

private class LinesSequence(private val reader: BufferedReader) : Sequence<String> {
    public override fun iterator(): Iterator<String> {
        return object : Iterator<String> {
            private var nextValue: String? = null
            private var done = false

            public override fun hasNext(): Boolean {
                if (nextValue == null && !done) {
                    nextValue = reader.readLine()
                    if (nextValue == null) done = true
                }
                return nextValue != null
            }

            public override fun next(): String {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val answer = nextValue
                nextValue = null
                return answer!!
            }
        }
    }
}