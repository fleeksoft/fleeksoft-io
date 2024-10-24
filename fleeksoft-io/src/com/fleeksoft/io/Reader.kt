package com.fleeksoft.io

expect abstract class Reader: Closeable {

    open fun read(): Int

    abstract fun read(cbuf: CharArray, offset: Int, length: Int): Int


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
    open fun skip(n: Long): Long

    /**
     * Tells whether this stream supports the mark() operation. The default
     * implementation always returns false. Subclasses should override this
     * method.
     *
     * @return true if and only if this stream supports the mark operation.
     */
    open fun markSupported(): Boolean


    /**
     * Tells whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise.  Note that returning false does not guarantee that the
     * next read will block.
     *
     * @throws     IOException  If an I/O error occurs
     */
    open fun ready(): Boolean

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
    open fun mark(readAheadLimit: Int)

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

    open fun reset()

    /**
     * Closes the stream and releases any system resources associated with
     * it.  Once the stream has been closed, further read(), ready(),
     * mark(), reset(), or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     *
     * @throws     IOException  If an I/O error occurs
     */
    abstract override fun close()
}