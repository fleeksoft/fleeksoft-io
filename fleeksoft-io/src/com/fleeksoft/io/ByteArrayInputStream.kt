package com.fleeksoft.io

expect open class ByteArrayInputStream : InputStream {
    constructor(buf: ByteArray)
    constructor(buf: ByteArray, offset: Int, length: Int)

    override fun read(): Int
}