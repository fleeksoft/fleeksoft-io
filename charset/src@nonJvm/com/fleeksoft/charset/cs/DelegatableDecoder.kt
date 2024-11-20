package com.fleeksoft.charset.cs

import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.charset.CoderResult

interface DelegatableDecoder {
    fun decodeLoop(src: ByteBuffer, dst: CharBuffer): CoderResult
    fun implReset()
    fun implFlush(out: CharBuffer): CoderResult
}