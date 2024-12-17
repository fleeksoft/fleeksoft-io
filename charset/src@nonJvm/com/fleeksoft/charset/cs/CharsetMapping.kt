/*
 * Copyright (c) 2008, 2023, Oracle and/or its affiliates. All rights reserved.
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

package com.fleeksoft.charset.cs

class CharsetMapping {
    //init from sjis0213.dat in SJIS_0213
    val b2cSB: CharArray? = null
    val b2cDB1: CharArray? = null
    val b2cDB2: CharArray? = null

    //min/max(start/end) value of 2nd byte
    val b2Min: Int? = null
    val b2Max: Int? = null

    //min/Max(start/end) value of 1st byte/db1
    val b1MinDB1: Int? = null
    val b1MaxDB1: Int? = null

    //min/Max(start/end) value of 1st byte/db2
    val b1MinDB2: Int? = null
    val b1MaxDB2: Int? = null

    val dbSegSize: Int? = null

    fun decodeSingle(b: Int): Char {
        return b2cSB!![b]
    }

    fun decodeDouble(b1: Int, b2: Int): Char {
        if (b2 in (b2Min!! until b2Max!!)) {
            var adjustedB2 = b2 - b2Min
            var adjustedB1 = b1
            if (b1 in (b1MinDB1!!..b1MaxDB1!!)) {
                adjustedB1 = b1 - b1MinDB1
                return b2cDB1!![adjustedB1 * dbSegSize!! + adjustedB2]
            }
            if (b1 in (b1MinDB2!!..b1MaxDB2!!)) {
                adjustedB1 = b1 - b1MinDB2
                return b2cDB2!![adjustedB1 * dbSegSize!! + adjustedB2]
            }
        }
        return UNMAPPABLE_DECODING
    }

    companion object {
        const val UNMAPPABLE_DECODING: Char = '\uFFFD'
        const val UNMAPPABLE_ENCODING: Int = 0xFFFD
        const val UNMAPPABLE_ENCODING_CHAR = UNMAPPABLE_ENCODING.toChar()
    }
}