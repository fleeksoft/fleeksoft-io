package com.fleeksoft.io

import com.fleeksoft.charset.Charset
import com.fleeksoft.charset.CharsetDecoder
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorAction
import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.internal.assert

class StreamDecoder : Reader {

    // -- Charset-based stream decoder impl --
    private var cs: Charset

    private var decoder: CharsetDecoder

    private var bb: ByteBuffer = ByteBufferFactory.allocate(Constants.DEFAULT_BYTE_BUFFER_SIZE)

    // Exactly one of these is non-null
    private var source: InputStream?

    private var closed = false

    constructor(input: InputStream, dec: CharsetDecoder) {
        this.cs = dec.charset()
        this.decoder = dec
        this.source = input
        this.bb = ByteBufferFactory.allocate(Constants.DEFAULT_BYTE_BUFFER_SIZE)
        this.bb.flipExt()
    }

    constructor(input: InputStream, charset: Charset = Charsets.UTF8) : this(
        input = input,
        dec = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
    )

    private fun ensureOpen() {
        if (closed) throw IOException("Stream closed")
    }

    // In order to handle surrogates properly we must never try to produce
    // fewer than two characters at a time.  If we're only asked to return one
    // character then the other is saved here to be returned later.
    //
    private var haveLeftoverChar: Boolean = false
    private var leftoverChar: Char? = null

    // -- Public methods corresponding to those in InputStreamReader --
    // All synchronization and state/argument checking is done in these public
    // methods; the concrete stream-decoder subclasses defined below need not
    // do any such checking.
    fun getEncoding(): String? {
        if (isOpen()) return encodingName()
        return null
    }

    override fun read(): Int {
        return read0()
    }

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        return lockedRead(cbuf, off, len)
    }

    private fun lockedRead(cbuf: CharArray, off: Int, len: Int): Int {
        var off = off
        var len = len
        ensureOpen()
        if ((off < 0) || (off > cbuf.size) || (len < 0) ||
            ((off + len) > cbuf.size) || ((off + len) < 0)
        ) {
            throw IndexOutOfBoundsException()
        }
        if (len == 0) return 0

        var n = 0

        if (haveLeftoverChar) {
            // Copy the leftover char into the buffer
            cbuf[off] = leftoverChar!!
            off++
            len--
            haveLeftoverChar = false
            n = 1
            if ((len == 0) || !implReady())  // Return now if this is all we can produce w/o blocking
                return n
        }

        if (len == 1) {
            // Treat single-character array reads just like read()
            val c = read0()
            if (c == -1) return if ((n == 0)) -1 else n
            cbuf[off] = c.toChar()
            return n + 1
        }


        // Read remaining characters
        val nr: Int = implRead(cbuf, off, off + len)


        // At this point, n is either 1 if a leftover character was read,
        // or 0 if no leftover character was read. If n is 1 and nr is -1,
        // indicating EOF, then we don't return their sum as this loses data.
        return if ((nr < 0)) (if (n == 1) 1 else nr) else (n + nr)
    }

    private fun read0(): Int {
        return lockedRead0()
    }

    private fun lockedRead0(): Int {
        // Return the leftover char, if there is one
        if (haveLeftoverChar) {
            haveLeftoverChar = false
            return leftoverChar!!.code
        }

        // Convert more bytes
        val cb = CharArray(2)
        when (val n = read(cb, 0, 2)) {
            -1 -> return -1
            2 -> {
                leftoverChar = cb[1]
                haveLeftoverChar = true
                return cb[0].code
            }

            1 -> return cb[0].code
            else -> {
                require(false) { "Unable to read from source" }
                return -1
            }
        }
    }

    private fun implRead(cbuf: CharArray, off: Int, end: Int): Int {
        // In order to handle surrogate pairs, this method requires that
        // the invoker attempt to read at least two characters.  Saving the
        // extra character, if any, at a higher level is easier than trying
        // to deal with it here.

        var cb = CharBufferFactory.wrap(charArray = cbuf, off = off, len = end - off)
        if (cb.position() != 0) {
            // Ensure that cb[0] == cbuf[off]
            cb = cb.slice()
        }

        var eof = false
        while (true) {
//            println("bbPosition: ${bb.position()}, bb${bb}")
            val cr = decoder.decode(bb, cb, eof)
            if (cr.isUnderflow()) {
                if (eof)
                    break
                if (!cb.hasRemaining())
                    break
                if ((cb.position() > 0) && !inReady())
                    break          // Block at most once
                val n = readBytes()
                if (n < 0) {
                    eof = true
                    if ((cb.position() == 0) && (!bb.hasRemaining()))
                        break
                }
                continue
            }
            if (cr.isOverflow()) {
                assert(cb.position() > 0)
                break
            }
            cr.throwException()
        }

        if (eof) {
            // ## Need to flush decoder
            decoder.reset()
        }

        if (cb.position() == 0) {
            if (eof) {
                return -1
            }
            assert(false)
        }
        return cb.position()
    }

    fun encodingName(): String {
        return cs.name()
    }

    private fun readBytes(): Int {
        bb.compact()
        try {
            // Read from the input stream, and then update the buffer
            val lim = bb.limit()
            val pos = bb.position()
            assert(pos <= lim)
            val remainingLength = if (pos <= lim) lim - pos else 0
            val n = source!!.read(bb.array(), bb.arrayOffset() + pos, remainingLength)
            if (n < 0) return n
            if (n == 0) throw IOException("Underlying input stream returned zero bytes")
            assert(n <= remainingLength) { "n = $n, rem = $remainingLength" }
            bb.position(pos + n)
        } finally {
            bb.flip()
        }

        val rem = bb.remaining()
        assert(rem != 0) { rem.toString() }
        return rem
    }

    override fun ready(): Boolean {
        ensureOpen()
        return haveLeftoverChar || implReady()
    }

    private fun inReady(): Boolean {
        return try {
            ((this.source != null) && (this.source!!.available() > 0)) // ## RBC.available()?
        } catch (x: IOException) {
            false
        }
    }

    private fun implReady(): Boolean {
        return !bb.hasRemaining() || inReady()
    }

    override fun close() {
        if (closed)
            return
        try {
            implClose()
        } finally {
            closed = true
        }
    }


    private fun implClose() {
        this.source?.close()
    }

    private fun isOpen(): Boolean {
        return !closed
    }

}