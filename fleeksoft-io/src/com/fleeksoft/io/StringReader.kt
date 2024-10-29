package com.fleeksoft.io

expect open class StringReader(str: String) : Reader {
    override fun read(cbuf: CharArray, off: Int, len: Int): Int
    override fun close()
}