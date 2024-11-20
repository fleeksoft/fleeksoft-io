package com.fleeksoft.charset

import com.fleeksoft.io.exception.BufferOverflowException
import com.fleeksoft.io.exception.BufferUnderflowException
import com.fleeksoft.io.exception.MalformedInputException
import com.fleeksoft.io.exception.UnmappableCharacterException

actual class CoderResult(private val type: Int, private val length: Int) {
    /**
     * Returns a string describing this coder result.
     *
     * @return  A descriptive string
     */
    actual override fun toString(): String {
        val nm: String = names[type]
        return if (this.isError()) "$nm[$length]" else nm
    }

    /**
     * Tells whether or not this object describes an underflow condition.
     *
     * @return  `true` if, and only if, this object denotes underflow
     */
    actual fun isUnderflow(): Boolean = type == CR_UNDERFLOW


    /**
     * Tells whether or not this object describes an overflow condition.
     *
     * @return  `true` if, and only if, this object denotes overflow
     */
    actual fun isOverflow(): Boolean = type == CR_OVERFLOW

    /**
     * Tells whether or not this object describes an error condition.
     *
     * @return  `true` if, and only if, this object denotes either a
     * malformed-input error or an unmappable-character error
     */
    actual fun isError(): Boolean = type >= CR_ERROR_MIN

    actual fun isMalformed(): Boolean = type == CR_MALFORMED
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
    actual fun isUnmappable(): Boolean = type == CR_UNMAPPABLE

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
    actual fun length(): Int {
        if (!this.isError()) throw UnsupportedOperationException()
        return length
    }

    private class Cache {
        // fixme:// use ConcurrentHashMap
        val unmappable: Map<Int, CoderResult> = HashMap()
        val malformed: Map<Int, CoderResult> = HashMap()

        companion object {
            val INSTANCE: Cache = Cache()
        }
    }

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
    actual fun throwException() {
        when (type) {
            CR_UNDERFLOW -> throw BufferUnderflowException()
            CR_OVERFLOW -> throw BufferOverflowException()
            CR_MALFORMED -> throw MalformedInputException(length)
            CR_UNMAPPABLE -> throw UnmappableCharacterException(length)
            else -> throw Exception("Unknown exception")
        }
    }

    companion object {
        private const val CR_UNDERFLOW = 0
        private const val CR_OVERFLOW = 1
        private const val CR_ERROR_MIN = 2
        private const val CR_MALFORMED = 2
        private const val CR_UNMAPPABLE = 3

        private val names = arrayOf<String>("UNDERFLOW", "OVERFLOW", "MALFORMED", "UNMAPPABLE")

        /**
         * Result object indicating underflow, meaning that either the input buffer
         * has been completely consumed or, if the input buffer is not yet empty,
         * that additional input is required.
         */
        val UNDERFLOW: CoderResult = CoderResult(CR_UNDERFLOW, 0)

        /**
         * Result object indicating overflow, meaning that there is insufficient
         * room in the output buffer.
         */
        val OVERFLOW: CoderResult = CoderResult(CR_OVERFLOW, 0)

        private val malformed4: Array<CoderResult> = arrayOf<CoderResult>(
            CoderResult(CR_MALFORMED, 1),
            CoderResult(CR_MALFORMED, 2),
            CoderResult(CR_MALFORMED, 3),
            CoderResult(CR_MALFORMED, 4),
        )

        /**
         * Static factory method that returns the unique object describing a
         * malformed-input error of the given length.
         *
         * @param   length
         * The given length
         *
         * @return  The requested coder-result object
         */
        fun malformedForLength(length: Int): CoderResult {
            require(length > 0) { "Non-positive length" }
            if (length <= 4) return malformed4[length - 1]
            return Cache.INSTANCE.malformed.getOrElse(length) { CoderResult(CR_MALFORMED, length) }
        }

        private val unmappable4: Array<CoderResult> = arrayOf<CoderResult>(
            CoderResult(CR_UNMAPPABLE, 1),
            CoderResult(CR_UNMAPPABLE, 2),
            CoderResult(CR_UNMAPPABLE, 3),
            CoderResult(CR_UNMAPPABLE, 4),
        )

        /**
         * Static factory method that returns the unique result object describing
         * an unmappable-character error of the given length.
         *
         * @param   length
         * The given length
         *
         * @return  The requested coder-result object
         */
        fun unmappableForLength(length: Int): CoderResult {
            require(length > 0) { "Non-positive length" }
            if (length <= 4) return unmappable4[length - 1]
            return Cache.INSTANCE.unmappable.getOrElse(length) { CoderResult(CR_UNMAPPABLE, length) }
        }
    }
}