package com.fleeksoft.charset

import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer

expect abstract class Charset : Comparable<Charset> {

    fun name(): String

    abstract fun newDecoder(): CharsetDecoder
    abstract fun newEncoder(): CharsetEncoder

    abstract fun contains(cs: Charset): Boolean

    fun decode(bb: ByteBuffer): CharBuffer

    fun encode(cb: CharBuffer): ByteBuffer

    fun encode(str: String): ByteBuffer

    open fun canEncode(): Boolean

    final override fun compareTo(other: Charset): Int

    final override fun equals(other: Any?): Boolean

    final override fun toString(): String

    final override fun hashCode(): Int
}