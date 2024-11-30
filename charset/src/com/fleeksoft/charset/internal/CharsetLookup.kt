package com.fleeksoft.charset.internal

import com.fleeksoft.charset.Charset

internal expect object CharsetLookup {
    fun forName(charsetName: String): Charset
    fun isSupported(charsetName: String): Boolean
}