package com.fleeksoft.io

expect open class StringReader(str: String) : Reader {
    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()
}