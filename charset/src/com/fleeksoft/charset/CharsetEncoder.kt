package com.fleeksoft.charset

import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.CharBuffer
import com.fleeksoft.io.exception.CoderMalfunctionError

/**
 * Initializes a new encoder.  The new encoder will have the given
 * bytes-per-char and replacement values.
 *
 * @param  cs
 * The charset that created this encoder
 *
 * @param  _averageBytesPerChar
 * A positive float value indicating the expected number of
 * bytes that will be produced for each input character
 *
 * @param  _maxBytesPerChar
 * A positive float value indicating the maximum number of
 * bytes that will be produced for each input character
 *
 * @param  replacement
 * The initial replacement; must not be `null`, must have
 * non-zero length, must not be longer than maxBytesPerChar,
 * and must be [legal][.isLegalReplacement]
 *
 * @throws  IllegalArgumentException
 * If the preconditions on the parameters do not hold
 */
expect abstract class CharsetEncoder {
    fun malformedInputAction(): CodingErrorAction
    fun unmappableCharacterAction(): CodingErrorAction

    /**
     * Returns the charset that created this encoder.
     *
     * @return  This encoder's charset
     */
    fun charset(): Charset

    /**
     * Returns this encoder's replacement value.
     *
     * @return  This encoder's current replacement,
     * which is never `null` and is never empty
     */
    fun replacement(): ByteArray

    /**
     * Changes this encoder's replacement value.
     *
     *
     *  This method invokes the [implReplaceWith][.implReplaceWith]
     * method, passing the new replacement, after checking that the new
     * replacement is acceptable.
     *
     * @param  newReplacement  The new replacement; must not be
     * `null`, must have non-zero length,
     *
     *
     *
     *
     *
     * must not be longer than the value returned by the
     * [maxBytesPerChar][.maxBytesPerChar] method, and
     * must be [legal][.isLegalReplacement]
     *
     *
     * @return  This encoder
     *
     * @throws  IllegalArgumentException
     * If the preconditions on the parameter do not hold
     */
    fun replaceWith(newReplacement: ByteArray): CharsetEncoder

    /**
     * Tells whether or not the given byte array is a legal replacement value
     * for this encoder.
     *
     *
     *  A replacement is legal if, and only if, it is a legal sequence of
     * bytes in this encoder's charset; that is, it must be possible to decode
     * the replacement into one or more sixteen-bit Unicode characters.
     *
     *
     *  The default implementation of this method is not very efficient; it
     * should generally be overridden to improve performance.
     *
     * @param  repl  The byte array to be tested
     *
     * @return  `true` if, and only if, the given byte array
     * is a legal replacement value for this encoder
     */
    open fun isLegalReplacement(repl: ByteArray): Boolean


    /**
     * Changes this encoder's action for malformed-input errors.
     *
     *
     *  This method invokes the [ implOnMalformedInput][.implOnMalformedInput] method, passing the new action.
     *
     * @param  newAction  The new action; must not be `null`
     *
     * @return  This encoder
     *
     * @throws IllegalArgumentException
     * If the precondition on the parameter does not hold
     */
    fun onMalformedInput(newAction: CodingErrorAction): CharsetEncoder

    /**
     * Changes this encoder's action for unmappable-character errors.
     *
     *
     *  This method invokes the [ implOnUnmappableCharacter][.implOnUnmappableCharacter] method, passing the new action.
     *
     * @param  newAction  The new action; must not be `null`
     *
     * @return  This encoder
     *
     * @throws IllegalArgumentException
     * If the precondition on the parameter does not hold
     */
    fun onUnmappableCharacter(newAction: CodingErrorAction): CharsetEncoder

    /**
     * Returns the average number of bytes that will be produced for each
     * character of input.  This heuristic value may be used to estimate the size
     * of the output buffer required for a given input sequence.
     *
     * @return  The average number of bytes produced
     * per character of input
     */
    fun averageBytesPerChar(): Float

    /**
     * Returns the maximum number of bytes that will be produced for each
     * character of input.  This value may be used to compute the worst-case size
     * of the output buffer required for a given input sequence. This value
     * accounts for any necessary content-independent prefix or suffix
     *
     * bytes, such as byte-order marks.
     *
     *
     *
     *
     *
     * @return  The maximum number of bytes that will be produced per
     * character of input
     */
    fun maxBytesPerChar(): Float

    /**
     * Encodes as many characters as possible from the given input buffer,
     * writing the results to the given output buffer.
     *
     *
     *  The buffers are read from, and written to, starting at their current
     * positions.  At most [in.remaining()][com.fleeksoft.charset.io.Buffer.remaining] characters
     * will be read and at most [out.remaining()][com.fleeksoft.charset.io.Buffer.remaining]
     * bytes will be written.  The buffers' positions will be advanced to
     * reflect the characters read and the bytes written, but their marks and
     * limits will not be modified.
     *
     *
     *  In addition to reading characters from the input buffer and writing
     * bytes to the output buffer, this method returns a [CoderResult]
     * object to describe its reason for termination:
     *
     *
     *
     *  *
     *
     * [CoderResultInternal.UNDERFLOW] indicates that as much of the
     * input buffer as possible has been encoded.  If there is no further
     * input then the invoker can proceed to the next step of the
     * [encoding operation](#steps).  Otherwise this method
     * should be invoked again with further input.
     *
     *  *
     *
     * [CoderResultInternal.OVERFLOW] indicates that there is
     * insufficient space in the output buffer to encode any more characters.
     * This method should be invoked again with an output buffer that has
     * more [remaining][com.fleeksoft.charset.io.Buffer.remaining] bytes. This is
     * typically done by draining any encoded bytes from the output
     * buffer.
     *
     *  *
     *
     * A [   malformed-input][CoderResultInternal.malformedForLength] result indicates that a malformed-input
     * error has been detected.  The malformed characters begin at the input
     * buffer's (possibly incremented) position; the number of malformed
     * characters may be determined by invoking the result object's [   ][CoderResult.length] method.  This case applies only if the
     * [malformed action][.onMalformedInput] of this encoder
     * is [CodingErrorAction.REPORT]; otherwise the malformed input
     * will be ignored or replaced, as requested.
     *
     *  *
     *
     * An [   unmappable-character][CoderResultInternal.unmappableForLength] result indicates that an
     * unmappable-character error has been detected.  The characters that
     * encode the unmappable character begin at the input buffer's (possibly
     * incremented) position; the number of such characters may be determined
     * by invoking the result object's [length][CoderResult.length]
     * method.  This case applies only if the [   unmappable action][.onUnmappableCharacter] of this encoder is [   ][CodingErrorAction.REPORT]; otherwise the unmappable character will be
     * ignored or replaced, as requested.
     *
     *
     *
     * In any case, if this method is to be reinvoked in the same encoding
     * operation then care should be taken to preserve any characters remaining
     * in the input buffer so that they are available to the next invocation.
     *
     *
     *  The `endOfInput` parameter advises this method as to whether
     * the invoker can provide further input beyond that contained in the given
     * input buffer.  If there is a possibility of providing additional input
     * then the invoker should pass `false` for this parameter; if there
     * is no possibility of providing further input then the invoker should
     * pass `true`.  It is not erroneous, and in fact it is quite
     * common, to pass `false` in one invocation and later discover that
     * no further input was actually available.  It is critical, however, that
     * the final invocation of this method in a sequence of invocations always
     * pass `true` so that any remaining unencoded input will be treated
     * as being malformed.
     *
     *
     *  This method works by invoking the [encodeLoop][.encodeLoop]
     * method, interpreting its results, handling error conditions, and
     * reinvoking it as necessary.
     *
     *
     * @param  `in`
     * The input character buffer
     *
     * @param  out
     * The output byte buffer
     *
     * @param  endOfInput
     * `true` if, and only if, the invoker can provide no
     * additional input characters beyond those in the given buffer
     *
     * @return  A coder-result object describing the reason for termination
     *
     * @throws  IllegalStateException
     * If an encoding operation is already in progress and the previous
     * step was an invocation neither of the [reset][.reset]
     * method, nor of this method with a value of `false` for
     * the `endOfInput` parameter, nor of this method with a
     * value of `true` for the `endOfInput` parameter
     * but a return value indicating an incomplete encoding operation
     *
     * @throws  CoderMalfunctionError
     * If an invocation of the encodeLoop method threw
     * an unexpected exception
     */
    fun encode(inCharBuffer: CharBuffer, out: ByteBuffer, endOfInput: Boolean): CoderResult

    /**
     * Flushes this encoder.
     *
     *
     *  Some encoders maintain internal state and may need to write some
     * final bytes to the output buffer once the overall input sequence has
     * been read.
     *
     *
     *  Any additional output is written to the output buffer beginning at
     * its current position.  At most [out.remaining()][com.fleeksoft.charset.io.Buffer.remaining]
     * bytes will be written.  The buffer's position will be advanced
     * appropriately, but its mark and limit will not be modified.
     *
     *
     *  If this method completes successfully then it returns [ ][CoderResultInternal.UNDERFLOW].  If there is insufficient room in the output
     * buffer then it returns [CoderResultInternal.OVERFLOW].  If this happens
     * then this method must be invoked again, with an output buffer that has
     * more room, in order to complete the current [encoding
 * operation](#steps).
     *
     *
     *  If this encoder has already been flushed then invoking this method
     * has no effect.
     *
     *
     *  This method invokes the [implFlush][.implFlush] method to
     * perform the actual flushing operation.
     *
     * @param  out
     * The output byte buffer
     *
     * @return  A coder-result object, either [CoderResultInternal.UNDERFLOW] or
     * [CoderResultInternal.OVERFLOW]
     *
     * @throws  IllegalStateException
     * If the previous step of the current encoding operation was an
     * invocation neither of the [flush][.flush] method nor of
     * the three-argument [          ][.encode] method
     * with a value of `true` for the `endOfInput`
     * parameter
     */
    fun flush(out: ByteBuffer): CoderResult

    /**
     * Resets this encoder, clearing any internal state.
     *
     *
     *  This method resets charset-independent state and also invokes the
     * [implReset][.implReset] method in order to perform any
     * charset-specific reset actions.
     *
     * @return  This encoder
     */
    fun reset(): CharsetEncoder

    /**
     * Convenience method that encodes the remaining content of a single input
     * character buffer into a newly-allocated byte buffer.
     *
     *
     *  This method implements an entire [encoding
 * operation](#steps); that is, it resets this encoder, then it encodes the
     * characters in the given character buffer, and finally it flushes this
     * encoder.  This method should therefore not be invoked if an encoding
     * operation is already in progress.
     *
     * @param  `in`
     * The input character buffer
     *
     * @return A newly-allocated byte buffer containing the result of the
     * encoding operation.  The buffer's position will be zero and its
     * limit will follow the last byte written.
     *
     * @throws  IllegalStateException
     * If an encoding operation is already in progress
     *
     * @throws  com.fleeksoft.charset.io.MalformedInputException
     * If the character sequence starting at the input buffer's current
     * position is not a legal sixteen-bit Unicode sequence and the current malformed-input action
     * is [CodingErrorAction.REPORT]
     *
     * @throws  com.fleeksoft.charset.io.UnmappableCharacterException
     * If the character sequence starting at the input buffer's current
     * position cannot be mapped to an equivalent byte sequence and
     * the current unmappable-character action is [          ][CodingErrorAction.REPORT]
     *
     * @throws  CharacterCodingException
     * `MalformedInputException` if the character sequence starting at the
     * input buffer's current position is not a legal sixteen-bit Unicode sequence and the current
     * malformed-input action is `CodingErrorAction.REPORT`;
     * `UnmappableCharacterException` if the character sequence starting at
     * the input buffer's current position cannot be mapped to an
     * equivalent byte sequence and the current unmappable-character
     * action is `CodingErrorAction.REPORT`
     *
     * @throws  OutOfMemoryError
     * If the output byte buffer for the requested size of the input
     * character buffer cannot be allocated
     */

    fun encode(cb: CharBuffer): ByteBuffer

    /**
     * Tells whether or not this encoder can encode the given character.
     *
     *
     *  This method returns `false` if the given character is a
     * surrogate character; such characters can be interpreted only when they
     * are members of a pair consisting of a high surrogate followed by a low
     * surrogate.  The [ canEncode(CharSequence)][.canEncode] method may be used to test whether or not a
     * character sequence can be encoded.
     *
     *
     *  This method may modify this encoder's state; it should therefore not
     * be invoked if an [encoding operation](#steps) is already in
     * progress.
     *
     *
     *  The default implementation of this method is not very efficient; it
     * should generally be overridden to improve performance.
     *
     * @param   c
     * The given character
     *
     * @return  `true` if, and only if, this encoder can encode
     * the given character
     *
     * @throws  IllegalStateException
     * If an encoding operation is already in progress
     */
    open fun canEncode(c: Char): Boolean

    /**
     * Tells whether or not this encoder can encode the given character
     * sequence.
     *
     *
     *  If this method returns `false` for a particular character
     * sequence then more information about why the sequence cannot be encoded
     * may be obtained by performing a full [encoding
 * operation](#steps).
     *
     *
     *  This method may modify this encoder's state; it should therefore not
     * be invoked if an encoding operation is already in progress.
     *
     *
     *  The default implementation of this method is not very efficient; it
     * should generally be overridden to improve performance.
     *
     * @param   cs
     * The given character sequence
     *
     * @return  `true` if, and only if, this encoder can encode
     * the given character without throwing any exceptions and without
     * performing any replacements
     *
     * @throws  IllegalStateException
     * If an encoding operation is already in progress
     */
    open fun canEncode(cs: CharSequence): Boolean
}