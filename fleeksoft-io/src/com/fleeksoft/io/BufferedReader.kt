package com.fleeksoft.io

/**
 * Creates a buffering character-input stream that uses an input buffer of
 * the specified size.
 *
 * @param  reader A Reader
 * @param sz Input-buffer size
 *
 * @throws IllegalArgumentException  If `sz <= 0`
 */
expect open class BufferedReader(reader: Reader, sz: Int) : Reader {
    constructor(reader: Reader)

    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()

    open fun readLine(): String?
}