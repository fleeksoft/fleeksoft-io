package com.fleeksoft.io

expect abstract class FilterReader protected constructor(reader: Reader) : Reader {
    override fun read(): Int
    override fun read(cbuf: CharArray, off: Int, len: Int): Int
    override fun skip(n: Long): Long
    override fun ready(): Boolean
    override fun markSupported(): Boolean
    override fun mark(readAheadLimit: Int)
    override fun reset()
    override fun close()
}