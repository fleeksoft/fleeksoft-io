package com.fleeksoft.io

expect class StringReader : Reader {
    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()
}