package com.fleeksoft.io

import com.fleeksoft.io.exception.IOException
import com.fleeksoft.io.exception.ReadOnlyBufferException
import kotlin.math.min

actual abstract class Reader protected actual constructor() : Readable, Closeable {

    actual open fun read(): Int {
        val cb = CharArray(1)
        return if (read(cb, 0, 1) == -1) -1
        else cb[0].code
    }

    actual abstract fun read(cbuf: CharArray, off: Int, len: Int): Int

    actual open fun read(cbuf: CharArray): Int {
        return read(cbuf, 0, cbuf.size)
    }

    actual override fun read(cb: CharBuffer): Int {
        if (cb.isReadOnly()) {
            throw ReadOnlyBufferException()
        }

        var nread: Int
        if (cb.hasArray()) {
            val cbuf = cb.array()
            val pos = cb.position()
            val rem = maxOf(cb.limit() - pos, 0)
            val off = cb.arrayOffset() + pos
            nread = this.read(cbuf, off, rem)
            if (nread > 0) {
                cb.position(pos + nread)
            }
        } else {
            val len = cb.remaining()
            val cbuf = CharArray(len)
            nread = read(cbuf, 0, len)
            if (nread > 0) {
                cb.put(cbuf, 0, nread)
            }
        }
        return nread
    }

    fun readString(length: Int): String {
        val cbuf = CharArray(length)
        read(cbuf, 0, cbuf.size)
        return cbuf.concatToString()
    }

    /** Skip buffer, null until allocated  */
    private var skipBuffer: CharArray? = null


    /**
     * Skips characters.  This method will block until some characters are
     * available, an I/O error occurs, or the end of the stream is reached.
     * If the stream is already at its end before this method is invoked,
     * then no characters are skipped and zero is returned.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @throws     IllegalArgumentException  If `n` is negative.
     * @throws     IOException  If an I/O error occurs
     */
    actual open fun skip(n: Long): Long {
        require(n >= 0L) { "skip value is negative" }
        return implSkip(n)
    }

    private fun implSkip(n: Long): Long {
        val nn = min(n.toInt(), Constants.maxSkipBufferSize)
        if ((skipBuffer == null) || (skipBuffer!!.size < nn)) skipBuffer = CharArray(nn)
        var r = n
        while (r > 0) {
            val nc = read(skipBuffer!!, 0, min(r.toDouble(), nn.toDouble()).toInt())
            if (nc == -1) break
            r -= nc.toLong()
        }
        return n - r
    }

    /**
     * Tells whether this stream supports the mark() operation. The default
     * implementation always returns false. Subclasses should override this
     * method.
     *
     * @return true if and only if this stream supports the mark operation.
     */
    actual open fun markSupported(): Boolean {
        return false
    }


    /**
     * Tells whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise.  Note that returning false does not guarantee that the
     * next read will block.
     *
     * @throws     IOException  If an I/O error occurs
     */
    actual open fun ready(): Boolean {
        return false
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.  Not all
     * character-input streams support the mark() operation.
     *
     * @param  readAheadLimit  Limit on the number of characters that may be
     * read while still preserving the mark.  After
     * reading this many characters, attempting to
     * reset the stream may fail.
     *
     * @throws     IOException  If the stream does not support mark(),
     * or if some other I/O error occurs
     */
    actual open fun mark(readAheadLimit: Int) {
        throw IOException("mark() not supported")
    }

    /**
     * Resets the stream.  If the stream has been marked, then attempt to
     * reposition it at the mark.  If the stream has not been marked, then
     * attempt to reset it in some way appropriate to the particular stream,
     * for example by repositioning it to its starting point.  Not all
     * character-input streams support the reset() operation, and some support
     * reset() without supporting mark().
     *
     * @throws     IOException  If the stream has not been marked,
     * or if the mark has been invalidated,
     * or if the stream does not support reset(),
     * or if some other I/O error occurs
     */

    actual open fun reset() {
        throw IOException("reset() not supported")
    }

    /**
     * Closes the stream and releases any system resources associated with
     * it.  Once the stream has been closed, further read(), ready(),
     * mark(), reset(), or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     *
     * @throws     IOException  If an I/O error occurs
     */
    actual abstract override fun close()
}