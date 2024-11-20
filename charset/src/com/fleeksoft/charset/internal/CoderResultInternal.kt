package com.fleeksoft.charset.internal

import com.fleeksoft.charset.CoderResult

expect object CoderResultInternal {
    val UNDERFLOW: CoderResult
    val OVERFLOW: CoderResult
    fun malformedForLength(length: Int): CoderResult
    fun unmappableForLength(length: Int): CoderResult
}