package com.fleeksoft.io

expect abstract class CharBuffer : Buffer, Comparable<CharBuffer>, Appendable, Readable {
    abstract fun asReadOnlyBuffer(): CharBuffer

    abstract fun get(): Char

//    abstract fun get(index: Int): Char

    open fun get(dstCharArray: CharArray, off: Int, len: Int): CharBuffer
    open fun get(index: Int, dstCharArray: CharArray, off: Int, len: Int): CharBuffer
    fun get(dstCharArray: CharArray): CharBuffer
    fun get(index: Int, dstCharArray: CharArray): CharBuffer
    abstract fun put(c: Char): CharBuffer
    abstract fun put(index: Int, c: Char): CharBuffer
    open fun put(src: CharArray, off: Int, len: Int): CharBuffer
    open fun put(index: Int, src: CharArray, off: Int, len: Int): CharBuffer
    fun put(src: CharArray): CharBuffer
    fun put(index: Int, src: CharArray): CharBuffer
    open fun put(src: CharBuffer): CharBuffer
    open fun put(index: Int, src: CharBuffer, off: Int, len: Int): CharBuffer
    fun put(src: String): CharBuffer
    open fun put(src: String, start: Int, end: Int): CharBuffer

    final override fun array(): CharArray
    final override fun hasArray(): Boolean
    final override fun arrayOffset(): Int

    abstract fun compact(): CharBuffer

    override fun toString(): String

    override fun compareTo(other: CharBuffer): Int
    override fun read(cb: CharBuffer): Int
    override fun append(value: CharSequence?): CharBuffer
    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): CharBuffer
    override fun append(value: Char): CharBuffer
}

expect fun CharBuffer.duplicateExt(): CharBuffer
expect fun CharBuffer.getChar(index: Int): Char