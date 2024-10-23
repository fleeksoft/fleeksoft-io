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
expect class BufferedReader(reader: Reader, sz: Int) : Reader {
    override fun read(cbuf: CharArray, offset: Int, length: Int): Int
    override fun close()
}