package com.fleeksoft.io

import kotlinx.io.Source

fun Source.inputStream(): InputStream = InputStreamKotlinx(this)