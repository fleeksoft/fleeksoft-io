package com.fleeksoft.charset.annotation

/**
 * Marks declarations that are **internal** in Charset's API.
 *
 * Targets marked by this annotation should not be used outside of Charset because their signatures
 * and semantics will change between future releases without any warnings and without providing
 * any migration aids.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class InternalCharsetApi