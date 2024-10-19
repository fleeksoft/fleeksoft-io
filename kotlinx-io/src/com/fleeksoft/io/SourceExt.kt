package com.fleeksoft.io

import kotlinx.io.Source


fun InputStream.Companion.from(source: Source): InputStream = InputStreamKotlinx(source)