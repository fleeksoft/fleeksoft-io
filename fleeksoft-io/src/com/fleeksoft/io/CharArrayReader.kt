package com.fleeksoft.io

expect class CharArrayReader: Reader {
    constructor(buf: CharArray)
    constructor(buf: CharArray,  off: Int, len: Int)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int
    override fun close()
}