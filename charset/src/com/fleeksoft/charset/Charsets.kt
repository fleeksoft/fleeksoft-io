@file:OptIn(InternalCharsetApi::class)

package com.fleeksoft.charset

import com.fleeksoft.charset.annotation.InternalCharsetApi
import com.fleeksoft.charset.internal.CharsetLookup

object Charsets {
    val UTF8 by lazy { forName("UTF-8") }
    val US_ASCII by lazy { forName("US-ASCII") }
    val ISO_8859_1 by lazy { forName("ISO-8859-1") }
    val UTF_16 by lazy { forName("UTF-16") }
    val UTF_32 by lazy { forName("UTF-32") }

    fun normalizeCharsetName(name: String): String = name.replace("-", "").replace("_", "").lowercase()
    fun forName(name: String): Charset {
        return CharsetLookup.forName(name)
    }

    fun isSupported(charsetName: String): Boolean {
        return CharsetLookup.isSupported(charsetName)
    }
}