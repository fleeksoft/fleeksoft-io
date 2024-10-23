package com.fleeksoft.io

expect abstract class InputStream() {

    abstract fun read(): Int

    public open fun readNBytes(len: Int): ByteArray

    public open fun read(bytes: ByteArray, off: Int, len: Int): Int

    public open fun readAllBytes(): ByteArray

    public open fun mark(readLimit: Int)

    public open fun reset()

    public open fun close()

    public open fun available(): Int

    public open fun markSupported(): Boolean
}