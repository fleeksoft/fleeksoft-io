package com.fleeksoft.io

import kotlinx.io.RawSource
import kotlinx.io.Source

public fun Source.asInputStream(): InputStream = SourceInputStream(this)
public fun InputStream.asSource(): RawSource = InputStreamSource(this)