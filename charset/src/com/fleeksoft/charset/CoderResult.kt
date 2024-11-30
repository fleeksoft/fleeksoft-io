package com.fleeksoft.charset

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.MalformedInputException
import com.fleeksoft.io.exception.UnmappableCharacterException

expect class CoderResult {
    /**
     * Returns a string describing this coder result.
     *
     * @return  A descriptive string
     */
    override fun toString(): String

    /**
     * Tells whether or not this object describes an underflow condition.
     *
     * @return  `true` if, and only if, this object denotes underflow
     */
    fun isUnderflow(): Boolean


    /**
     * Tells whether or not this object describes an overflow condition.
     *
     * @return  `true` if, and only if, this object denotes overflow
     */
    fun isOverflow(): Boolean

    /**
     * Tells whether or not this object describes an error condition.
     *
     * @return  `true` if, and only if, this object denotes either a
     * malformed-input error or an unmappable-character error
     */
    fun isError(): Boolean

    fun isMalformed(): Boolean
    /**
     * Tells whether or not this object describes a malformed-input error.
     *
     * @return  `true` if, and only if, this object denotes a
     * malformed-input error
     */

    /**
     * Tells whether or not this object describes an unmappable-character
     * error.
     *
     * @return  `true` if, and only if, this object denotes an
     * unmappable-character error
     */
    fun isUnmappable(): Boolean

    /**
     * Returns the length of the erroneous input described by this
     * object&nbsp;&nbsp;*(optional operation)*.
     *
     * @return  The length of the erroneous input, a positive integer
     *
     * @throws  UnsupportedOperationException
     * If this object does not describe an error condition, that is,
     * if the [isError][.isError] does not return `true`
     */
    fun length(): Int

    /**
     * Throws an exception appropriate to the result described by this object.
     *
     * @throws  Exceptions
     * If this object is [.UNDERFLOW]
     *
     * @throws  BufferOverflowException
     * If this object is [.OVERFLOW]
     *
     * @throws  MalformedInputException
     * If this object represents a malformed-input error; the
     * exception's length value will be that of this object
     *
     * @throws  UnmappableCharacterException
     * If this object represents an unmappable-character error; the
     * exception's length value will be that of this object
     *
     * @throws  CharacterCodingException
     * `MalformedInputException` if this object represents a
     * malformed-input error; `UnmappableCharacterException`
     * if this object represents an unmappable-character error
     */
    fun throwException()
}