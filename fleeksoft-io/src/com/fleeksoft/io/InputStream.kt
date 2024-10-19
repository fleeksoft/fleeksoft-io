package com.fleeksoft.io

interface InputStream {

    public abstract fun mark(readLimit: Long)

    public abstract fun reset()

    public abstract fun readBytes(count: Int): ByteArray

    public abstract fun read(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Int

    public abstract fun readAllBytes(): ByteArray

    public abstract fun exhausted(): Boolean

    public abstract fun close()

    companion object
}