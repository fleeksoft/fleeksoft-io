package com.fleeksoft.charset.spi

import com.fleeksoft.charset.annotation.InternalCharsetApi

@InternalCharsetApi
expect object CharsetProviderRegistry {
    val providers: List<CharsetServiceProviderLoader>
    fun register(provider: CharsetServiceProviderLoader)
}

@InternalCharsetApi
interface CharsetServiceProviderLoader {
    val factory: CharsetProvider
    fun prioity(): Int = 0
}