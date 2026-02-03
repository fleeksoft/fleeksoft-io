@file:OptIn(ExperimentalContracts::class)

package com.fleeksoft.io

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.toByteArray
import kotlin.contracts.ExperimentalContracts

public inline fun String.byteInputStream(charset: Charset = Charsets.UTF8): ByteArrayInputStream =
    ByteArrayInputStream(toByteArray(charset))

public inline fun InputStream.reader(charset: Charset = Charsets.UTF8): InputStreamReader =
    InputStreamReader(this, charset)

public inline fun InputStream.bufferedReader(charset: Charset = Charsets.UTF8): BufferedReader =
    reader(charset).buffered()