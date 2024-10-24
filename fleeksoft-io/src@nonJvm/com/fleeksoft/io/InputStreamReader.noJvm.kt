package com.fleeksoft.io

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.Charsets

actual class InputStreamReader : Reader {

    private val sd: StreamDecoder

    actual constructor(inputStream: InputStream) : this(inputStream, Charsets.UTF8)
    actual constructor(inputStream: InputStream, charsetName: String) : this(inputStream, Charsets.forName(charsetName))
    actual constructor(inputStream: InputStream, charset: Charset) {
        sd = StreamDecoder(inputStream, charset)
    }

    /**
     * Returns the name of the character encoding being used by this stream.
     *
     *
     *  If the encoding has an historical name then that name is returned;
     * otherwise the encoding's canonical name is returned.
     *
     *
     *  If this instance was created with the [ ][.InputStreamReader] constructor then the returned
     * name, being unique for the encoding, may differ from the name passed to
     * the constructor. This method will return `null` if the
     * stream has been closed.
     *
     * @return The historical name of this encoding, or
     * `null` if the stream has been closed
     *
     * @see Charset
     */
    fun getEncoding(): String? {
        return sd.getEncoding()
    }

    /**
     * Reads a single character.
     *
     * @return The character read, or -1 if the end of the stream has been
     *         reached
     *
     * @throws IOException If an I/O error occurs
     */
    override fun read(): Int {
        return sd.read()
    }

    actual override fun read(cbuf: CharArray, offset: Int, length: Int): Int {
        return sd.read(cbuf, offset, length)
    }

    override fun ready(): Boolean {
        return sd.ready()
    }

    actual override fun close() {
        sd.close()
    }
}