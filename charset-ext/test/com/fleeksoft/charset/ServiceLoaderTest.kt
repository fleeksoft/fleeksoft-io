@file:OptIn(InternalCharsetApi::class)

package com.fleeksoft.charset

import com.fleeksoft.charset.annotation.InternalCharsetApi
import com.fleeksoft.charset.spi.CharsetProviderRegistry
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ServiceLoaderTest {
    @Test
    fun testServiceLoader() {
        if (Platform.isJvmOrAndroid()) {
            assertFailsWith<UnsupportedOperationException> {
                CharsetProviderRegistry.providers.isEmpty()
            }
        } else {
            assertTrue(CharsetProviderRegistry.providers.isNotEmpty())
        }
    }
}