package com.fleeksoft.charset.internal

import com.fleeksoft.charset.CoderResult

actual object CoderResultInternal {
    actual val UNDERFLOW: CoderResult = CoderResult.UNDERFLOW
    actual val OVERFLOW: CoderResult = CoderResult.OVERFLOW
    actual fun malformedForLength(length: Int): CoderResult = CoderResult.malformedForLength(length)
    actual fun unmappableForLength(length: Int): CoderResult = CoderResult.unmappableForLength(length)
}