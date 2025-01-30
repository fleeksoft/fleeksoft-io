/*
 * Copyright (c) 2001, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

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