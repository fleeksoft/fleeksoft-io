package com.fleeksoft.io

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder

expect open class InputStreamReader : Reader {
    constructor(inputStream: InputStream)
    constructor(inputStream: InputStream, charsetName: String)
    constructor(inputStream: InputStream, charset: Charset)
    constructor(inputStream: InputStream, dec: CharsetDecoder)

    override fun read(cbuf: CharArray, off: Int, len: Int): Int
    override fun close()
}