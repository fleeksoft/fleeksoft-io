package com.fleeksoft.io

expect abstract class InputStream() : Closeable {

    abstract fun read(): Int

    public open fun read(bytes: ByteArray, off: Int, len: Int): Int

    public open fun read(bytes: ByteArray): Int

    public open fun readNBytes(len: Int): ByteArray

    public open fun readNBytes(bytes: ByteArray, off: Int, len: Int): Int

    public open fun readAllBytes(): ByteArray

    public open fun mark(readLimit: Int)

    public open fun reset()

    public open fun skip(n: Long): Long

    public open fun skipNBytes(n: Long)

    public override fun close()

    public open fun available(): Int

    public open fun markSupported(): Boolean
}