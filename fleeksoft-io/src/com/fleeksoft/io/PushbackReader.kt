package com.fleeksoft.io

expect open class PushbackReader: FilterReader {
    constructor(reader: Reader)
    constructor(reader: Reader, size: Int)

    override fun read(): Int
    override fun read(cbuf: CharArray, off: Int, len: Int): Int
    open fun unread(c: Int)
    open fun unread(cbuf: CharArray, off: Int, len: Int)
    open fun unread(cbuf: CharArray)
    override fun ready(): Boolean
    override fun mark(readAheadLimit: Int)
    override fun reset()
    override fun markSupported(): Boolean
    override fun close()
    override fun skip(n: Long): Long
}