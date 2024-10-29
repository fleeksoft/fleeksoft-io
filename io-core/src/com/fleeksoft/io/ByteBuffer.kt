package com.fleeksoft.io

expect abstract class ByteBuffer : Buffer, Comparable<ByteBuffer> {
    abstract fun asReadOnlyBuffer(): ByteBuffer

    abstract fun get(): Byte
    abstract fun get(index: Int): Byte
    fun get(dst: ByteArray): ByteBuffer
    fun get(index: Int, dst: ByteArray): ByteBuffer
    open fun get(dst: ByteArray, off: Int, len: Int): ByteBuffer
    open fun get(index: Int, dst: ByteArray, off: Int, len: Int): ByteBuffer

    abstract fun put(b: Byte): ByteBuffer
    abstract fun put(index: Int, b: Byte): ByteBuffer
    fun put(src: ByteArray): ByteBuffer
    fun put(index: Int, src: ByteArray): ByteBuffer
    open fun put(src: ByteArray, off: Int, len: Int): ByteBuffer
    open fun put(index: Int, src: ByteArray, off: Int, len: Int): ByteBuffer
    fun put(src: ByteBuffer): ByteBuffer
    fun put(index: Int, src: ByteBuffer, off: Int, len: Int): ByteBuffer

    final override fun array(): ByteArray
    final override fun hasArray(): Boolean
    final override fun arrayOffset(): Int

    abstract fun compact(): ByteBuffer

    override fun compareTo(other: ByteBuffer): Int
}

fun ByteBuffer.getInt(): Int = get().toInt()
expect fun ByteBuffer.duplicateExt(): ByteBuffer