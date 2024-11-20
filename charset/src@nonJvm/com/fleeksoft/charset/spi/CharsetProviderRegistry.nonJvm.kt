package com.fleeksoft.charset.spi

import com.fleeksoft.charset.annotation.InternalCharsetApi
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

@InternalCharsetApi
actual object CharsetProviderRegistry {
    private val lock = SynchronizedObject()
    private val _providers = mutableListOf<CharsetServiceProviderLoader>()
    actual val providers: List<CharsetServiceProviderLoader>
        get() = synchronized(lock) { _providers.toList().sortedByDescending { it.prioity() } }

    actual fun register(provider: CharsetServiceProviderLoader) = synchronized(lock) {
        _providers += provider
    }
}