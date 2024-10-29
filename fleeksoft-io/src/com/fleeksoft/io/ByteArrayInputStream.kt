package com.fleeksoft.io

expect open class ByteArrayInputStream : InputStream {
    constructor(buf: ByteArray)
    constructor(buf: ByteArray, off: Int, len: Int)

    override fun read(): Int
}