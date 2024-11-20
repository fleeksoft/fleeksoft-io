package com.fleeksoft.charset.spi

import com.fleeksoft.charset.annotation.InternalCharsetApi


@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization
@InternalCharsetApi
@Deprecated("", level = DeprecationLevel.HIDDEN)
val initHook: Any = CharsetProviderRegistry.register(ExtendedCharsetServiceProvider())