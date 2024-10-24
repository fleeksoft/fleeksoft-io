package com.fleeksoft.io

import com.fleeksoft.charset.Charset

expect class InputStreamReader: Reader {
    constructor(inputStream: InputStream)
    constructor(inputStream: InputStream, charsetName: String)
    constructor(inputStream: InputStream, charset: Charset)

    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()
}