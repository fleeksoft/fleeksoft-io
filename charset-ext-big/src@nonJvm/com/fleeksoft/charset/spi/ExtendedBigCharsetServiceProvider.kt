package com.fleeksoft.charset.spi

import com.fleeksoft.charset.annotation.InternalCharsetApi

@InternalCharsetApi
internal class ExtendedBigCharsetServiceProvider : CharsetServiceProviderLoader {
    override val factory: CharsetProvider by lazy { ExtendedBigCharsetProvider() }
}