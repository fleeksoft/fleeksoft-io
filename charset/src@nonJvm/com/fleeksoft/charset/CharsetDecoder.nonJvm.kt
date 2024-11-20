package com.fleeksoft.charset

import com.fleeksoft.charset.internal.ArraysSupport
import com.fleeksoft.charset.internal.CoderResultInternal
import com.fleeksoft.charset.internal.assert
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.CharBufferFactory
import com.fleeksoft.io.exception.CoderMalfunctionError
import kotlin.math.min

actual abstract class CharsetDecoder protected constructor(
    private val _charset: Charset,
    private val averageCharsPerByte: Float,
    private val maxCharsPerByte: Float
) {

    init {
        require(averageCharsPerByte > 0.0f) { "averageCharsPerByte must be positive, was $averageCharsPerByte" }
        require(maxCharsPerByte > 0.0f) { "maxCharsPerByte must be positive, was $maxCharsPerByte" }
        require(!(averageCharsPerByte > maxCharsPerByte)) { "averageCharsPerByte must not greater than maxCharsPerByte, was $averageCharsPerByte" }
    }

    private var state: Int = ST_RESET

    private var _malformedInputAction: CodingErrorAction = CodingErrorAction.REPORT
    private var _unmappableCharacterAction: CodingErrorAction = CodingErrorAction.REPORT


    private var _replacement: String = "\uFFFD"

    actual fun charset() = _charset
    actual fun maxCharsPerByte() = maxCharsPerByte
    actual fun averageCharsPerByte() = averageCharsPerByte
    actual fun malformedInputAction() = _malformedInputAction
    actual fun unmappableCharacterAction() = _unmappableCharacterAction
    protected fun replacement() = _replacement
    protected open fun implReplaceWith(newReplacement: String) {}

    actual fun decode(byteBuffer: ByteBuffer, outCharBuffer: CharBuffer, endOfInput: Boolean): CoderResult {
        val newState: Int = if (endOfInput) ST_END else ST_CODING
        if ((state != ST_RESET) && (state != ST_CODING)
            && !(endOfInput && (state == ST_END))
        ) throwIllegalStateException(state, newState)
        state = newState

        while (true) {
            var cr: CoderResult
            try {
                cr = decodeLoop(byteBuffer, outCharBuffer)
            } catch (x: RuntimeException) {
                throw CoderMalfunctionError(x)
            }

            if (cr.isOverflow()) return cr

            if (cr.isUnderflow()) {
                if (endOfInput && byteBuffer.hasRemaining()) {
                    cr = CoderResultInternal.malformedForLength(byteBuffer.remaining())
                    // Fall through to malformed-input case
                } else {
                    return cr
                }
            }

            var action: CodingErrorAction? = null
            if (cr.isMalformed()) action = _malformedInputAction
            else if (cr.isUnmappable()) action = _unmappableCharacterAction
            else assert(false) { cr.toString() }

            if (action == CodingErrorAction.REPORT) return cr

            if (action == CodingErrorAction.REPLACE) {
                if (outCharBuffer.remaining() < _replacement.length) return CoderResultInternal.OVERFLOW
                outCharBuffer.put(_replacement)
            }

            if ((action == CodingErrorAction.IGNORE) || (action == CodingErrorAction.REPLACE)) {
                // Skip erroneous input either way
                byteBuffer.position(byteBuffer.position() + cr.length())
                continue
            }

            assert(false)
        }
    }

    actual fun decode(inByteBuffer: ByteBuffer): CharBuffer {
        var n: Int = min((inByteBuffer.remaining() * averageCharsPerByte).toInt(), ArraysSupport.SOFT_MAX_ARRAY_LENGTH)
        var out: CharBuffer = CharBufferFactory.allocate(n)

        if ((n == 0) && (inByteBuffer.remaining() == 0)) return out
        reset()
        while (true) {
            var cr: CoderResult =
                if (inByteBuffer.hasRemaining()) decode(inByteBuffer, out, true) else CoderResultInternal.UNDERFLOW
            if (cr.isUnderflow()) cr = flush(out)

            if (cr.isUnderflow()) break
            if (cr.isOverflow()) {
                // Ensure progress; n might be 0!
                n = ArraysSupport.newLength(n, min(n + 1, 1024), n + 1)
                val o: CharBuffer = CharBufferFactory.allocate(n)
                out.flip()
                o.put(out)
                out = o
                continue
            }
            cr.throwException()
        }
        out.flip()
        return out
    }

    actual fun flush(out: CharBuffer): CoderResult {
        if (state == ST_END) {
            val cr: CoderResult = implFlush(out)
            if (cr.isUnderflow()) state = ST_FLUSHED
            return cr
        }

        if (state != ST_FLUSHED) throwIllegalStateException(state, ST_FLUSHED)

        return CoderResultInternal.UNDERFLOW // Already flushed
    }

    protected abstract fun decodeLoop(byteBuffer: ByteBuffer, charBuffer: CharBuffer): CoderResult

    private fun throwIllegalStateException(from: Int, to: Int) {
        throw IllegalStateException("Current state = ${stateNames[from]}, new state = ${stateNames[to]}")
    }

    /**
     * Flushes this decoder.
     *
     * <p> The default implementation of this method does nothing, and always
     * returns {@link CoderResult#UNDERFLOW}.  This method should be overridden
     * by decoders that may need to write final characters to the output buffer
     * once the entire input sequence has been read. </p>
     *
     * @param  out
     *         The output character buffer
     *
     * @return  A coder-result object, either {@link CoderResult#UNDERFLOW} or
     *          {@link CoderResult#OVERFLOW}
     */
    protected open fun implFlush(out: CharBuffer): CoderResult {
        return CoderResultInternal.UNDERFLOW
    }

    actual fun onMalformedInput(newAction: CodingErrorAction): CharsetDecoder {
        _malformedInputAction = newAction
        implOnMalformedInput(newAction)
        return this
    }

    protected open fun implOnMalformedInput(newAction: CodingErrorAction) {}

    actual fun onUnmappableCharacter(newAction: CodingErrorAction): CharsetDecoder {
        _unmappableCharacterAction = newAction
        implOnUnmappableCharacter(newAction)
        return this
    }

    protected open fun implOnUnmappableCharacter(newAction: CodingErrorAction) {}

    /**
     * Resets this decoder, clearing any internal state.
     *
     * This method resets charset-independent state and also invokes the
     * [implReset] method to perform any charset-specific reset actions.
     *
     * @return This decoder
     */
    actual fun reset(): CharsetDecoder {
        implReset()
        state = ST_RESET
        return this
    }

    /**
     * Resets this decoder, clearing any charset-specific internal state.
     *
     * The default implementation of this method does nothing. This method
     * should be overridden by decoders that maintain internal state.
     */
    protected open fun implReset() {}

    companion object {
        const val ST_RESET = 0
        const val ST_CODING = 1
        const val ST_END = 2
        const val ST_FLUSHED = 3

        val stateNames: Array<String> = arrayOf("RESET", "CODING", "CODING_END", "FLUSHED")
    }
}
