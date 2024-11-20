@file:OptIn(InternalCharsetApi::class)

package com.fleeksoft.charset.internal

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.annotation.InternalCharsetApi
import com.fleeksoft.charset.spi.CharsetProviderRegistry

internal actual object CharsetLookup {
    private val standardProvider = StandardCharsetsProvider()

    private var cache1: ArrayList<Any> = arrayListOf() // level 1 cache
    private var cache2: ArrayList<Any> = arrayListOf() // level 2 cache


    actual fun forName(charsetName: String): Charset {
        return lookup(Charsets.normalizeCharsetName(charsetName)) ?: throw Exception("Unknown charset: $charsetName")
    }

    actual fun isSupported(charsetName: String): Boolean = lookup(Charsets.normalizeCharsetName(charsetName)) != null

    private fun cache(charsetName: String, cs: Charset) {
        cache2 = cache1
        cache1 = arrayListOf(charsetName, cs)
    }

    private fun lookup(charsetName: String): Charset? {
        if (cache1.isNotEmpty() && charsetName == cache1[0]) return cache1[1] as Charset
        // We expect most programs to use one Charset repeatedly.
        // We convey a hint to this effect to the VM by putting the
        // level 1 cache miss code in a separate method.
        return lookup2(charsetName)
    }

    private fun lookup2(charsetName: String): Charset? {
        if (cache2.isNotEmpty() && charsetName == cache2[0]) {
            val a = cache2
            cache2 = cache1
            cache1 = a
            return a[1] as Charset
        }
        var cs: Charset? = null
        val stdKey = CharsetNameMapping.standardCharsetMapKeys[charsetName]
        if (stdKey != null && (standardProvider.charsetForName(stdKey).also { cs = it }) != null) {
            cache(charsetName, cs!!)
            return cs
        }

        val extKey = CharsetNameMapping.extendedCharsetMapKeys[charsetName]
        if (extKey != null) {
            CharsetProviderRegistry.providers.forEach { provider ->
                if ((provider.factory.charsetForName(extKey).also { cs = it }) != null) {
                    cache(charsetName, cs!!)
                    return cs
                }
            }
        }

        return null
    }
}