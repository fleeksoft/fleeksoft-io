package com.fleeksoft.io.okio

import com.fleeksoft.io.InputStream
import okio.BufferedSource
import okio.Source
import okio.buffer

public fun Source.asInputStream(): InputStream = OkioSourceInputStream(this as? BufferedSource ?: this.buffer())
public fun InputStream.asSource(): Source = OkioInputStreamSource(this)