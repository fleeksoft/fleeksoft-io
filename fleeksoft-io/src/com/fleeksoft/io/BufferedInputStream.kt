package com.fleeksoft.io

expect open class BufferedInputStream: FilterInputStream {
    constructor(input: InputStream)
    constructor(input: InputStream, size: Int)

    override fun read(): Int
    override fun read(bytes: ByteArray, off: Int, len: Int): Int
    override fun skip(n: Long): Long
    override fun available(): Int
    override fun mark(readLimit: Int)
    override fun reset()
    override fun markSupported(): Boolean
    override fun close()
}