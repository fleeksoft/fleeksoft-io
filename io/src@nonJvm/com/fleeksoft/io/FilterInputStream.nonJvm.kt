package com.fleeksoft.io

import kotlin.concurrent.Volatile

actual open class FilterInputStream protected actual constructor(@Volatile protected open var input: InputStream?) : InputStream() {
    /**
     * {@inheritDoc}
     * @implSpec
     * This method simply performs `in.read()` and returns the result.
     *
     * @return     {@inheritDoc}
     * @throws com.fleeksoft.io.exception.IOException  {@inheritDoc}
     */
    actual override fun read(): Int {
        return input!!.read()
    }

    /**
     * Reads up to `b.length` bytes of data from this
     * input stream into an array of bytes. This method blocks until some
     * input is available.
     *
     * @implSpec
     * This method simply performs the call
     * `read(b, 0, b.length)` and returns
     * the result. It is important that it does
     * *not* do `in.read(b)` instead;
     * certain subclasses of  `FilterInputStream`
     * depend on the implementation strategy actually
     * used.
     *
     * @param      bytes   {@inheritDoc}
     * @return     {@inheritDoc}
     * @throws com.fleeksoft.io.exception.IOException  if an I/O error occurs.
     */
    actual override fun read(bytes: ByteArray): Int {
        return read(bytes, 0, bytes.size)
    }

    /**
     * Reads up to `len` bytes of data from this input stream
     * into an array of bytes. If `len` is not zero, the method
     * blocks until some input is available; otherwise, no
     * bytes are read and `0` is returned.
     *
     * @implSpec
     * This method simply performs `in.read(b, off, len)`
     * and returns the result.
     *
     * @param      bytes     {@inheritDoc}
     * @param      off   {@inheritDoc}
     * @param      len   {@inheritDoc}
     * @return     {@inheritDoc}
     * @throws     NullPointerException {@inheritDoc}
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     * @throws com.fleeksoft.io.exception.IOException  if an I/O error occurs.
     */
    actual override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        return input!!.read(bytes, off, len)
    }

    /**
     * Skips over and discards `n` bytes of data from the
     * input stream. The `skip` method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes,
     * possibly `0`. The actual number of bytes skipped is
     * returned.
     *
     * @implSpec
     * This method simply performs `in.skip(n)` and returns the result.
     *
     * @param      n   {@inheritDoc}
     * @return     the actual number of bytes skipped.
     * @throws com.fleeksoft.io.exception.IOException  if `in.skip(n)` throws an IOException.
     */
    actual override fun skip(n: Long): Long {
        return input!!.skip(n)
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * caller of a method for this input stream. The next caller might be
     * the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     *
     * @implSpec
     * This method returns the result of `in.available()`.
     *
     * @return     an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking.
     * @throws com.fleeksoft.io.exception.IOException  {@inheritDoc}
     */
    actual override fun available(): Int {
        return input!!.available()
    }

    /**
     * {@inheritDoc}
     * @implSpec
     * This method simply performs `in.close()`.
     *
     * @throws com.fleeksoft.io.exception.IOException  {@inheritDoc}
     */
    actual override fun close() {
        input?.close()
    }

    /**
     * Marks the current position in this input stream. A subsequent
     * call to the `reset` method repositions this stream at
     * the last marked position so that subsequent reads re-read the same bytes.
     *
     *
     * The `readlimit` argument tells this input stream to
     * allow that many bytes to be read before the mark position gets
     * invalidated.
     *
     * @implSpec
     * This method simply performs `in.mark(readlimit)`.
     *
     * @param   readLimit   {@inheritDoc}
     */
    actual override fun mark(readLimit: Int) {
        input?.mark(readLimit)
    }

    /**
     * Repositions this stream to the position at the time the
     * `mark` method was last called on this input stream.
     *
     *
     * Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parse, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails.
     * If this happens within readlimit bytes, it allows the outer
     * code to reset the stream and try another parser.
     *
     * @implSpec
     * This method simply performs `in.reset()`.
     *
     * @throws com.fleeksoft.io.exception.IOException  {@inheritDoc}
     */
    actual override fun reset() {
        input?.reset()
    }

    /**
     * Tests if this input stream supports the `mark`
     * and `reset` methods.
     *
     * @implSpec
     * This method simply performs `in.markSupported()`.
     *
     * @return  `true` if this stream type supports the
     * `mark` and `reset` method;
     * `false` otherwise.
     */
    actual override fun markSupported(): Boolean {
        return input!!.markSupported()
    }

}