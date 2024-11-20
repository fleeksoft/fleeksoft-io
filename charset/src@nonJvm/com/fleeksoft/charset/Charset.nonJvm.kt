@file:OptIn(InternalCharsetApi::class)

package com.fleeksoft.charset

import com.fleeksoft.charset.annotation.InternalCharsetApi
import com.fleeksoft.charset.cs.ThreadLocalCoders
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory

actual abstract class Charset protected constructor(private val csName: String, val aliases: Array<String>?) :
    Comparable<Charset> {
    actual fun name() = csName

    actual abstract fun newDecoder(): CharsetDecoder
    actual abstract fun newEncoder(): CharsetEncoder

    actual abstract fun contains(cs: Charset): Boolean

    actual fun decode(bb: ByteBuffer): CharBuffer {
        return ThreadLocalCoders.decoderFor(this)
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
            .decode(bb)
    }

    actual fun encode(cb: CharBuffer): ByteBuffer {
        return ThreadLocalCoders.encoderFor(this)
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
            .encode(cb)
    }

    actual fun encode(str: String): ByteBuffer {
        return encode(CharBufferFactory.wrap(str))
    }

    actual open fun canEncode(): Boolean = true


    actual override fun compareTo(other: Charset): Int {
        return csName.compareTo(other.csName, ignoreCase = true)
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        return other is Charset && csName == other.csName
    }

    actual override fun toString(): String {
        return csName
    }

    actual override fun hashCode(): Int {
        return csName.hashCode()
    }
}