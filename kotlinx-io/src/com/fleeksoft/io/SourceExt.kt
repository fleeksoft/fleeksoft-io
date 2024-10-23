package com.fleeksoft.io

import kotlinx.io.Source

fun Source.toInputStream(): InputStream = InputStreamKotlinx(this)