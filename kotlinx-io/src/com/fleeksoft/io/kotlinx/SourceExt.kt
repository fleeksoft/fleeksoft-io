package com.fleeksoft.io.kotlinx

import com.fleeksoft.io.InputStream
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered

public fun Source.asInputStream(): InputStream = SourceInputStream(this)
public fun RawSource.asInputStream(): InputStream = SourceInputStream(this.buffered())
public fun InputStream.asSource(): RawSource = InputStreamSource(this)