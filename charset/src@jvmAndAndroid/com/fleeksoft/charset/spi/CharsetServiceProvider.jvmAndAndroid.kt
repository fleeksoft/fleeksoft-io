package com.fleeksoft.charset.spi

import com.fleeksoft.charset.annotation.InternalCharsetApi

@InternalCharsetApi
actual object CharsetProviderRegistry {
    /*actual val providers: List<CharsetServiceProviderLoader> by lazy {
        ServiceLoader.load(
            CharsetServiceProviderLoader::class.java,
            CharsetServiceProviderLoader::class.java.classLoader
        ).sortedByDescending { it.prioity() }.toList()
    }*/

    actual val providers: List<CharsetServiceProviderLoader>
        get() = throw UnsupportedOperationException("This is not supported for JVM")

    actual fun register(provider: CharsetServiceProviderLoader) {
        throw UnsupportedOperationException("This is not supported for JVM")
    }
}