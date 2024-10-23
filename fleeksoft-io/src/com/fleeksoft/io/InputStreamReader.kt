package com.fleeksoft.io

expect class InputStreamReader: Reader {
    constructor(inputStream: InputStream)

    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()
}